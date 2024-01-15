package com.nowhere.springauthserver.api.dto;

import java.util.List;

public record UserResponse(String username, List<String> roles) {
}