package com.tournament.exception;

/** Исключение для конфликта с уже существующими данными. */
public class ConflictException extends TournamentException {
    public ConflictException(String message) {
        this(message, "Проверьте конфликтующие данные и устраните причину перед повторным запросом");
    }

    public ConflictException(String message, String suggestion) {
        this(message, suggestion, "CONFLICT");
    }

    public ConflictException(String message, String suggestion, String errorCode) {
        super(message, errorCode, suggestion);
    }
}
