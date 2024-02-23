package com.nowhere.springauthserver.api.dto;

import jakarta.validation.constraints.Email;

public record RegisterForm(String email, String password, String confirmPassword) {

    public RegisterForm {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (confirmPassword == null || confirmPassword.isBlank()) {
            throw new IllegalArgumentException("Confirm Password cannot be null or empty");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password and Confirm Password must be the same");
        }
    }

}
