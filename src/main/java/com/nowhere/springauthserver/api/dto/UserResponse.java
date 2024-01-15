package com.nowhere.springauthserver.api.dto;

public record UserResponse(String uuid, String username, boolean isEnabled) {
}
