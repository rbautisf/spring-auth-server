package com.nowhere.springauthserver.api.dto;

import java.util.List;

public record UserResponse(String uuid, String username, boolean isEnabled, List<String> roles) {
}
