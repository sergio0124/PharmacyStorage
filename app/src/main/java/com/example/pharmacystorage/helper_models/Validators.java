package com.example.pharmacystorage.helper_models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java email validation program
 *
 * @author pankaj
 *
 */
public class Validators {
    // Email Regex java
    private static final String EMAIL_REGEX = "^(.+)@(\\S+)$";

    // static Pattern object, since pattern is fixed
    private static Pattern patternEmail = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

    // non-static Matcher object because it's created from the input String

    /**
     * This method validates the input email address with EMAIL_REGEX pattern
     *
     * @param email
     * @return boolean
     */
    public static boolean validateEmail(String email) {
        Matcher matcher = patternEmail.matcher(email);
        return matcher.matches();
    }

    // digit + lowercase char + uppercase char + punctuation + symbol
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()â€[{}]:;',?/*~$^+=<>]).{4,20}$";

    private static final Pattern patternPassword = Pattern.compile(PASSWORD_PATTERN);

    public static boolean validatePassword(final String password) {
        Matcher matcher = patternPassword.matcher(password);
        return matcher.matches();
    }

    private static final String LOGIN_PATTERN =
            "^[A-Za-z]([.A-Za-z0-9-]{3,18})([A-Za-z0-9])$";

    private static final Pattern patternLogin = Pattern.compile(PASSWORD_PATTERN);

    public static boolean validateLogin(final String login) {
        Matcher matcher = patternLogin.matcher(login);
        return matcher.matches();
    }
}
