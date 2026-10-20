package com.tournament.model.enums;

/**
 * Жизненный цикл спортивного матча.
 */
public enum MatchStatus {
    SCHEDULED("Запланирован"),
    IN_PROGRESS("В процессе"),
    FINISHED("Завершён"),
    CANCELLED("Отменён");

    private final String displayName;

    MatchStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
