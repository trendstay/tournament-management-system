package com.tournament.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

/** Единый формат REST-ответа для ошибок приложения. */
public class ErrorResponse {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;
    private final int status;
    private final String errorCode;
    private final String message;
    private final String suggestion;
    private Map<String, Object> details;

    public ErrorResponse(int status, String errorCode, String message, String suggestion) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.suggestion = suggestion;
        this.details = Map.of();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}
