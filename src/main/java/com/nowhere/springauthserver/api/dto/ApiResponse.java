package com.nowhere.springauthserver.api.dto;

public record ApiResponse<T>(int status, T data) {
}
