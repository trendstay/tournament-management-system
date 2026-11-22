package com.tournament.exception;

import java.util.List;

/** Исключение для некорректных входных данных или аргументов. */
public class ValidationException extends TournamentException {
    private final List<String> validationErrors;

    public ValidationException(String message) {
        this(message, "Проверьте корректность введённых данных и повторите запрос");
    }

    public ValidationException(String message, String suggestion) {
        super(message, "VALIDATION_ERROR", suggestion);
        this.validationErrors = List.of(message);
    }

    public ValidationException(List<String> errors) {
        super("Ошибка валидации: " + String.join("; ", errors),
                "VALIDATION_ERROR", "Исправьте перечисленные ошибки и повторите запрос");
        this.validationErrors = List.copyOf(errors);
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
