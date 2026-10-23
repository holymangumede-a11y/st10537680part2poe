package com.mycompany.main;

import java.util.Scanner;

// ===== PART 2: extra imports =====
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    // Scanner for user input
    static Scanner input = new Scanner(System.in);

    // Store user details
    static String username;
    static String password;
    static String cellphone;

    // Store registered details
    static String registeredUsername;
    static String registeredPassword;
    static String registeredCellphone;

    // Check username
    // Username must be exactly 5 characters and contain an underscore
    public static boolean checkUserName(String username) {
        return username.length() == 5 && username.contains("_");
    }

    // Check password complexity
    // Password must:
    // - Have at least 8 characters
    // - Have an uppercase letter
    // - Have a lowercase letter
    // - Have a number
    // - Have a special character
    public static boolean checkPasswordComplexity(String password) {

        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    // Check cellphone number
    // Must start with +27 and contain exactly 9 digits after +27
    public static boolean checkCellphoneNumber(String number) {
        return number.matches("^\\+27[0-9]{9}$");
    }

    // Registration method
    public static void registerUser() {

        boolean registrationSuccessful = false;

        while (!registrationSuccessful) {

            System.out.println("\n--- Registration ---");

            // Ask for username
            System.out.print("Enter your username: ");
            username = input.nextLine();

            // Ask for password
            System.out.print("Enter your password: ");
            password = input.nextLine();

            // Ask for cellphone number
            System.out.print("Enter your cellphone number (+27): ");
            cellphone = input.nextLine();

            // Check each detail
            boolean usernameCorrect = checkUserName(username);
            boolean passwordCorrect = checkPasswordComplexity(password);
            boolean phoneCorrect = checkCellphoneNumber(cellphone);

            // Check if everything is correct
            if (usernameCorrect && passwordCorrect && phoneCorrect) {

                // Save the registered details
                registeredUsername = username;
                registeredPassword = password;
                registeredCellphone = cellphone;

                System.out.println("\nUser registered successfully!");

                registrationSuccessful = true;

            } else {

                System.out.println("\nRegistration failed. Please check the following:");

                if (!usernameCorrect) {
                    System.out.println(
                        "- Username must be exactly 5 characters "
                        + "and contain an underscore."
                    );
                }

                if (!passwordCorrect) {
                    System.out.println(
                        "- Password must be at least 8 characters and contain "
                        + "an uppercase letter, lowercase letter, number, "
                        + "and special character."
                    );
                }

                if (!phoneCorrect) {
                    System.out.println(
                        "- Cellphone number must start with +27 "
                        + "followed by exactly 9 digits."
                    );
                }

                System.out.println("\nPlease enter your details again.");
            }
        }
    }

    // Login method
    public static boolean loginUser(
            String loginUsername,
            String loginPassword) {

        // Compare login details with registered details
        return loginUsername.equals(registeredUsername)
                && loginPassword.equals(registeredPassword);
    }

    // Return login status
    public static String returnLoginStatus(boolean loginSuccessful) {

        if (loginSuccessful) {
            return "Welcome, it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    // ================================================================
    // PART 2: MESSAGING FEATURE (added below)
    // ================================================================

    // A simple class to hold the details of one message
    static class Message {
        int messageNumber;
        String messageID;
        String messageHash;
        String recipient;
        String text;

        Message(int messageNumber, String messageID, String messageHash,
                String recipient, String text) {
            this.messageNumber = messageNumber;
            this.messageID = messageID;
            this.messageHash = messageHash;
            this.recipient = recipient;
            this.text = text;
        }
    }

    // ArrayList that stores all the messages that were sent
    static ArrayList<Message> sentMessages = new ArrayList<>();

    // Random object used to create message IDs
    static Random random = new Random();

    // Create a 10 digit Message ID using string manipulation (substring)
    public static String createMessageID() {
        // Add zeros in front, then keep only the last 10 characters
        String padded = "0000000000" + Math.abs(random.nextLong());
        return padded.substring(padded.length() - 10);
    }

    // Create the Message Hash using string manipulation
    // Format: first two digits of ID : message number : FIRSTWORDLASTWORD
    public static String createMessageHash(String messageID,
                                           int messageNumber,
                                           String text) {

        // Split the message into words
        String[] words = text.trim().split(" ");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        // First two characters of the ID
        String firstTwo = messageID.substring(0, 2);

        return (firstTwo + ":" + messageNumber + ":" + firstWord + lastWord)
                .toUpperCase();
    }

    // Build the text that shows the details of one message
    public static String displayMessage(Message message) {
        return "Message Number: " + message.messageNumber
                + "\nMessage ID: " + message.messageID
                + "\nMessage Hash: " + message.messageHash
                + "\nRecipient: " + message.recipient
                + "\nMessage: " + message.text;
    }

    // Make text safe to place inside a JSON string
    public static String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Save all messages to a JSON file
    // The JSON layout was written by hand following the standard JSON
    // format described at https://www.json.org (add any other sources you use)
    public static void saveMessagesToJson() {

        StringBuilder json = new StringBuilder();
        json.append("[\n");

        int counter = 0;

        // For-each loop goes through every stored message
        for (Message message : sentMessages) {

            counter++;

            json.append("  {\n");
            json.append("    \"messageNumber\": " + message.messageNumber + ",\n");
            json.append("    \"messageID\": \"" + message.messageID + "\",\n");
            json.append("    \"messageHash\": \"" + escapeJson(message.messageHash) + "\",\n");
            json.append("    \"recipient\": \"" + escapeJson(message.recipient) + "\",\n");
            json.append("    \"message\": \"" + escapeJson(message.text) + "\"\n");
            json.append("  }");

            // No comma after the last message
            if (counter < sentMessages.size()) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("]");

        // Write the JSON text to a file
        try (FileWriter writer = new FileWriter("messages.json")) {
            writer.write(json.toString());
            System.out.println("\nMessages saved to messages.json");
        } catch (IOException e) {
            System.out.println("Could not save the JSON file.");
        }
    }

    // Add messages: a for loop lets the user enter the assigned number
    public static void sendMessages() {

        // Ask how many messages will be entered
        int howMany = 0;

        while (howMany < 1) {
            System.out.print("\nHow many messages do you want to enter? ");

            try {
                howMany = Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                howMany = 0;
            }

            if (howMany < 1) {
                System.out.println("Please enter a whole number of 1 or more.");
            }
        }

        // For loop: runs once for each message
        for (int i = 1; i <= howMany; i++) {

            System.out.println("\n--- Message " + i + " of " + howMany + " ---");

            // Ask for the recipient (reuses the cellphone check from Part 1)
            String recipient = "";
            while (!checkCellphoneNumber(recipient)) {
                System.out.print("Enter recipient cellphone number (+27): ");
                recipient = input.nextLine();

                if (!checkCellphoneNumber(recipient)) {
                    System.out.println("Number must start with +27 followed by 9 digits.");
                }
            }

            // Ask for the message text (must not be empty)
            String text = "";
            while (text.trim().isEmpty()) {
                System.out.print("Enter your message: ");
                text = input.nextLine();

                if (text.trim().isEmpty()) {
                    System.out.println("The message cannot be empty.");
                }
            }

            // The loop counter is used as the message number
            int messageNumber = i;
            String messageID = createMessageID();
            String messageHash = createMessageHash(messageID, messageNumber, text);

            // Store the message in the ArrayList
            Message newMessage = new Message(
                    messageNumber, messageID, messageHash, recipient, text);
            sentMessages.add(newMessage);

            System.out.println("\nMessage sent:");
            System.out.println(displayMessage(newMessage));
        }

        // Save everything to the JSON file
        saveMessagesToJson();
    }

    // Show all sent messages using a for-each loop
    public static void showSentMessages() {

        if (sentMessages.isEmpty()) {
            System.out.println("\nNo messages have been sent yet.");
            return;
        }

        System.out.println("\n--- Sent Messages ---");

        // For-each loop: goes through every message in the ArrayList
        for (Message message : sentMessages) {
            System.out.println(displayMessage(message));
            System.out.println("----------------------");
        }
    }

    // Welcome message and menu (runs in a while loop until the user quits)
    public static void startQuickChat() {

        System.out.println("\nWelcome to QuickChat.");

        boolean running = true;

        while (running) {

            System.out.println("\n--- Menu ---");
            System.out.println("1) Send messages");
            System.out.println("2) Show sent messages");
            System.out.println("3) Quit");
            System.out.print("Choose an option (1-3): ");

            String choice = input.nextLine().trim();

            if (choice.equals("1")) {
                sendMessages();
            } else if (choice.equals("2")) {
                showSentMessages();
            } else if (choice.equals("3")) {
                System.out.println("Goodbye!");
                running = false;
            } else {
                System.out.println("Invalid option. Please enter 1, 2 or 3.");
            }
        }
    }

    // ================================================================
    // END OF PART 2 ADDITIONS
    // ================================================================

    // Main method
    public static void main(String[] args) {

        // Register the user
        registerUser();

        // Login section
        System.out.println("\n--- Login ---");

        System.out.print("Enter username to login: ");
        String loginUsernameInput = input.nextLine();

        System.out.print("Enter password to login: ");
        String loginPasswordInput = input.nextLine();

        // Check login
        boolean loginSuccessful = loginUser(
                loginUsernameInput,
                loginPasswordInput
        );

        // Display login result
        System.out.println(returnLoginStatus(loginSuccessful));

        // PART 2: only start QuickChat if the login worked
        if (loginSuccessful) {
            startQuickChat();
        }
    }
}
