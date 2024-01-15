package com.nowhere.springauthserver.api.dto;

import org.springframework.http.HttpStatusCode;

public record ApiResponse<T>(int status, HttpStatusCode httpStatusCode, String message, T data) {

    public ApiResponse(int status, String message) {
        this(status, HttpStatusCode.valueOf(status), message, null);
    }

    public ApiResponse(int status, T data) {
        this(status, HttpStatusCode.valueOf(status), "", data);
    }

}
