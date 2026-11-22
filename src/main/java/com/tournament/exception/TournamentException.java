package com.tournament.exception;

/** Базовое исключение приложения: код ошибки и подсказка для её исправления. */
public class TournamentException extends RuntimeException {
    private final String errorCode;
    private final String suggestion;

    public TournamentException(String message, String errorCode, String suggestion) {
        super(message);
        this.errorCode = errorCode;
        this.suggestion = suggestion;
    }

    public TournamentException(String message, String errorCode, String suggestion, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.suggestion = suggestion;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getSuggestion() {
        return suggestion;
    }
}
