package com.tournament.exception;

/** Исключение для ситуации, когда запрошенный объект не найден. */
public class NotFoundException extends TournamentException {
    private static final String DEFAULT_SUGGESTION =
            "Проверьте идентификатор: возможно, объект был удалён или ещё не создан";

    public NotFoundException(String message) {
        this(message, DEFAULT_SUGGESTION);
    }

    public NotFoundException(String entityName, Long id) {
        this(entityName + " с ID " + id + " не найден(а)");
    }

    public NotFoundException(String message, String suggestion) {
        super(message, "NOT_FOUND", suggestion);
    }
}
