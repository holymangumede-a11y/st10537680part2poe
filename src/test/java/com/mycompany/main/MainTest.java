package com.mycompany.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit 5 tests for Main.
 *
 * Main keeps its state in static fields and reads from a static Scanner, so
 * these tests replace Main.input with a Scanner over simulated keyboard input
 * and capture System.out to check what the program prints.
 */
public class MainTest {

    // Known-good details used across the tests
    private static final String VALID_USERNAME = "kyl_1";
    private static final String VALID_PASSWORD = "Ch&&sec@ke99!";
    private static final String VALID_CELLPHONE = "+27838968976";

    // Messages printed by Main (kept here so the tests read clearly)
    private static final String USERNAME_ERROR =
            "Username must be exactly 5 characters and contain an underscore.";
    private static final String PASSWORD_ERROR =
            "Password must be at least 8 characters and contain "
            + "an uppercase letter, lowercase letter, number, "
            + "and special character.";
    private static final String CELLPHONE_ERROR =
            "Cellphone number must start with +27 followed by exactly 9 digits.";
    private static final String REGISTER_SUCCESS = "User registered successfully!";
    private static final String LOGIN_SUCCESS = "Welcome, it is great to see you again.";
    private static final String LOGIN_FAILURE = "Username or password incorrect, please try again.";

    private Scanner originalInput;
    private PrintStream originalOut;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void setUp() {
        // Remember the real Scanner and System.out so they can be restored
        originalInput = Main.input;
        originalOut = System.out;

        // Capture everything the program prints
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));

        // Start every test with a clean "database"
        Main.registeredUsername = null;
        Main.registeredPassword = null;
        Main.registeredCellphone = null;
    }

    @AfterEach
    void tearDown() {
        Main.input = originalInput;
        System.setOut(originalOut);
    }

    // ---------- Helpers ----------

    /** Feeds the given lines to Main as if the user typed them. */
    private void simulateInput(String... lines) {
        String text = String.join("\n", lines) + "\n";
        Main.input = new Scanner(
                new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
    }

    /** Everything printed to System.out so far. */
    private String output() {
        return capturedOut.toString();
    }

    // ---------- checkUserName ----------

    @Test
    void checkUserName_validUsername_returnsTrue() {
        assertTrue(Main.checkUserName("kyl_1"));
    }

    @Test
    void checkUserName_underscoreAtAnyPosition_returnsTrue() {
        assertTrue(Main.checkUserName("_abcd"));
        assertTrue(Main.checkUserName("abcd_"));
        assertTrue(Main.checkUserName("ab_cd"));
    }

    @Test
    void checkUserName_noUnderscore_returnsFalse() {
        assertFalse(Main.checkUserName("kyle1"));
    }

    @Test
    void checkUserName_tooShort_returnsFalse() {
        assertFalse(Main.checkUserName("ky_1"));
    }

    @Test
    void checkUserName_tooLong_returnsFalse() {
        assertFalse(Main.checkUserName("kyle_1"));
    }

    @Test
    void checkUserName_emptyString_returnsFalse() {
        assertFalse(Main.checkUserName(""));
    }

    // ---------- checkPasswordComplexity ----------

    @Test
    void checkPasswordComplexity_validPassword_returnsTrue() {
        assertTrue(Main.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    void checkPasswordComplexity_exactlyEightCharacters_returnsTrue() {
        assertTrue(Main.checkPasswordComplexity("Pass1!ab"));
    }

    @Test
    void checkPasswordComplexity_tooShort_returnsFalse() {
        // 7 characters, otherwise meets every rule
        assertFalse(Main.checkPasswordComplexity("Pass1!a"));
    }

    @Test
    void checkPasswordComplexity_noUppercase_returnsFalse() {
        assertFalse(Main.checkPasswordComplexity("password1!"));
    }

    @Test
    void checkPasswordComplexity_noLowercase_returnsFalse() {
        assertFalse(Main.checkPasswordComplexity("PASSWORD1!"));
    }

    @Test
    void checkPasswordComplexity_noNumber_returnsFalse() {
        assertFalse(Main.checkPasswordComplexity("Password!!"));
    }

    @Test
    void checkPasswordComplexity_noSpecialCharacter_returnsFalse() {
        assertFalse(Main.checkPasswordComplexity("Password12"));
    }

    @Test
    void checkPasswordComplexity_emptyString_returnsFalse() {
        assertFalse(Main.checkPasswordComplexity(""));
    }

    // ---------- checkCellphoneNumber ----------

    @Test
    void checkCellphoneNumber_validNumber_returnsTrue() {
        assertTrue(Main.checkCellphoneNumber("+27838968976"));
    }

    @Test
    void checkCellphoneNumber_missingInternationalCode_returnsFalse() {
        assertFalse(Main.checkCellphoneNumber("0838968976"));
        assertFalse(Main.checkCellphoneNumber("27838968976"));
    }

    @Test
    void checkCellphoneNumber_tooFewDigits_returnsFalse() {
        assertFalse(Main.checkCellphoneNumber("+2783896897"));
    }

    @Test
    void checkCellphoneNumber_tooManyDigits_returnsFalse() {
        assertFalse(Main.checkCellphoneNumber("+278389689761"));
    }

    @Test
    void checkCellphoneNumber_containsLetters_returnsFalse() {
        assertFalse(Main.checkCellphoneNumber("+2783896897a"));
    }

    @Test
    void checkCellphoneNumber_containsSpaces_returnsFalse() {
        assertFalse(Main.checkCellphoneNumber("+27 838968976"));
    }

    @Test
    void checkCellphoneNumber_wrongCountryCode_returnsFalse() {
        assertFalse(Main.checkCellphoneNumber("+44838968976"));
    }

    @Test
    void checkCellphoneNumber_emptyString_returnsFalse() {
        assertFalse(Main.checkCellphoneNumber(""));
    }

    // ---------- registerUser ----------

    @Test
    void registerUser_validDetails_savesDetailsAndPrintsSuccess() {
        simulateInput(VALID_USERNAME, VALID_PASSWORD, VALID_CELLPHONE);

        Main.registerUser();

        assertEquals(VALID_USERNAME, Main.registeredUsername);
        assertEquals(VALID_PASSWORD, Main.registeredPassword);
        assertEquals(VALID_CELLPHONE, Main.registeredCellphone);
        assertTrue(output().contains(REGISTER_SUCCESS));
        assertFalse(output().contains("Registration failed"));
    }

    @Test
    void registerUser_allDetailsInvalid_showsAllErrorsThenAcceptsValidRetry() {
        simulateInput(
                "bad", "weak", "0838968976",                  // 1st attempt: all invalid
                VALID_USERNAME, VALID_PASSWORD, VALID_CELLPHONE // 2nd attempt: valid
        );

        Main.registerUser();

        String out = output();
        assertTrue(out.contains("Registration failed"));
        assertTrue(out.contains(USERNAME_ERROR));
        assertTrue(out.contains(PASSWORD_ERROR));
        assertTrue(out.contains(CELLPHONE_ERROR));
        assertTrue(out.contains(REGISTER_SUCCESS));

        // Only the valid second attempt is stored
        assertEquals(VALID_USERNAME, Main.registeredUsername);
        assertEquals(VALID_PASSWORD, Main.registeredPassword);
        assertEquals(VALID_CELLPHONE, Main.registeredCellphone);
    }

    @Test
    void registerUser_onlyUsernameInvalid_showsOnlyUsernameError() {
        simulateInput(
                "kyle1", VALID_PASSWORD, VALID_CELLPHONE,
                VALID_USERNAME, VALID_PASSWORD, VALID_CELLPHONE
        );

        Main.registerUser();

        String out = output();
        assertTrue(out.contains(USERNAME_ERROR));
        assertFalse(out.contains(PASSWORD_ERROR));
        assertFalse(out.contains(CELLPHONE_ERROR));
        assertEquals(VALID_USERNAME, Main.registeredUsername);
    }

    @Test
    void registerUser_onlyPasswordInvalid_showsOnlyPasswordError() {
        simulateInput(
                VALID_USERNAME, "password", VALID_CELLPHONE,
                VALID_USERNAME, VALID_PASSWORD, VALID_CELLPHONE
        );

        Main.registerUser();

        String out = output();
        assertFalse(out.contains(USERNAME_ERROR));
        assertTrue(out.contains(PASSWORD_ERROR));
        assertFalse(out.contains(CELLPHONE_ERROR));
        assertEquals(VALID_PASSWORD, Main.registeredPassword);
    }

    @Test
    void registerUser_onlyCellphoneInvalid_showsOnlyCellphoneError() {
        simulateInput(
                VALID_USERNAME, VALID_PASSWORD, "12345",
                VALID_USERNAME, VALID_PASSWORD, VALID_CELLPHONE
        );

        Main.registerUser();

        String out = output();
        assertFalse(out.contains(USERNAME_ERROR));
        assertFalse(out.contains(PASSWORD_ERROR));
        assertTrue(out.contains(CELLPHONE_ERROR));
        assertEquals(VALID_CELLPHONE, Main.registeredCellphone);
    }

    @Test
    void registerUser_failedAttempt_doesNotSaveInvalidDetails() {
        simulateInput(
                "bad", "weak", "123",
                VALID_USERNAME, VALID_PASSWORD, VALID_CELLPHONE
        );

        Main.registerUser();

        assertFalse("bad".equals(Main.registeredUsername));
        assertFalse("weak".equals(Main.registeredPassword));
        assertFalse("123".equals(Main.registeredCellphone));
    }

    // ---------- loginUser ----------

    @Test
    void loginUser_correctCredentials_returnsTrue() {
        Main.registeredUsername = VALID_USERNAME;
        Main.registeredPassword = VALID_PASSWORD;

        assertTrue(Main.loginUser(VALID_USERNAME, VALID_PASSWORD));
    }

    @Test
    void loginUser_wrongPassword_returnsFalse() {
        Main.registeredUsername = VALID_USERNAME;
        Main.registeredPassword = VALID_PASSWORD;

        assertFalse(Main.loginUser(VALID_USERNAME, "WrongPass1!"));
    }

    @Test
    void loginUser_wrongUsername_returnsFalse() {
        Main.registeredUsername = VALID_USERNAME;
        Main.registeredPassword = VALID_PASSWORD;

        assertFalse(Main.loginUser("oth_r", VALID_PASSWORD));
    }

    @Test
    void loginUser_bothWrong_returnsFalse() {
        Main.registeredUsername = VALID_USERNAME;
        Main.registeredPassword = VALID_PASSWORD;

        assertFalse(Main.loginUser("oth_r", "WrongPass1!"));
    }

    @Test
    void loginUser_isCaseSensitive() {
        Main.registeredUsername = VALID_USERNAME;
        Main.registeredPassword = VALID_PASSWORD;

        assertFalse(Main.loginUser("KYL_1", VALID_PASSWORD));
        assertFalse(Main.loginUser(VALID_USERNAME, VALID_PASSWORD.toLowerCase()));
    }

    @Test
    void loginUser_noRegisteredUser_returnsFalse() {
        assertNull(Main.registeredUsername);
        assertFalse(Main.loginUser(VALID_USERNAME, VALID_PASSWORD));
    }

    @Test
    void loginUser_emptyCredentials_returnsFalse() {
        Main.registeredUsername = VALID_USERNAME;
        Main.registeredPassword = VALID_PASSWORD;

        assertFalse(Main.loginUser("", ""));
    }

    // ---------- returnLoginStatus ----------

    @Test
    void returnLoginStatus_success_returnsWelcomeMessage() {
        assertEquals(LOGIN_SUCCESS, Main.returnLoginStatus(true));
    }

    @Test
    void returnLoginStatus_failure_returnsErrorMessage() {
        assertEquals(LOGIN_FAILURE, Main.returnLoginStatus(false));
    }

    // ---------- main (full register + login flow) ----------

    @Test
    void main_registerThenLoginWithCorrectDetails_printsWelcome() {
        simulateInput(
                VALID_USERNAME, VALID_PASSWORD, VALID_CELLPHONE, // registration
                VALID_USERNAME, VALID_PASSWORD                   // login
        );

        Main.main(new String[0]);

        String out = output();
        assertTrue(out.contains(REGISTER_SUCCESS));
        assertTrue(out.contains(LOGIN_SUCCESS));
        assertFalse(out.contains(LOGIN_FAILURE));
    }

    @Test
    void main_registerThenLoginWithWrongPassword_printsFailure() {
        simulateInput(
                VALID_USERNAME, VALID_PASSWORD, VALID_CELLPHONE, // registration
                VALID_USERNAME, "WrongPass1!"                    // login
        );

        Main.main(new String[0]);

        String out = output();
        assertTrue(out.contains(REGISTER_SUCCESS));
        assertTrue(out.contains(LOGIN_FAILURE));
        assertFalse(out.contains(LOGIN_SUCCESS));
    }

    @Test
    void main_invalidRegistrationIsRetriedBeforeLogin() {
        simulateInput(
                "bad", "weak", "123",                            // failed registration
                VALID_USERNAME, VALID_PASSWORD, VALID_CELLPHONE, // successful registration
                VALID_USERNAME, VALID_PASSWORD                   // login
        );

        Main.main(new String[0]);

        String out = output();
        assertTrue(out.contains("Registration failed"));
        assertTrue(out.contains(REGISTER_SUCCESS));
        assertTrue(out.contains(LOGIN_SUCCESS));
    }
}
