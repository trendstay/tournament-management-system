package com.tournament.exception;

/** Исключение для операции, недоступной пользователю в текущей роли или состоянии. */
public class AccessDeniedException extends TournamentException {
    private final String operation;

    public AccessDeniedException(String operation, String reason) {
        this(operation, reason,
                "Выберите пользователя с подходящей ролью или измените назначение объекта перед операцией");
    }

    public AccessDeniedException(String operation, String reason, String suggestion) {
        super(String.format("Операция «%s» недоступна: %s", operation, reason),
                "ACCESS_DENIED", suggestion);
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}
