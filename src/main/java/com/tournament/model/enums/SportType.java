package com.tournament.model.enums;

/**
 * Виды спорта, поддерживаемые сервисом турниров.
 */
public enum SportType {
    FOOTBALL("Футбол", true),
    BASKETBALL("Баскетбол", false),
    TENNIS("Теннис", false),
    CHESS("Шахматы", true),
    E_SPORTS("Киберспорт", true);

    private final String displayName;
    private final boolean allowsDraw; // Разрешена ли ничья по правилам вида спорта

    SportType(String displayName, boolean allowsDraw) {
        this.displayName = displayName;
        this.allowsDraw = allowsDraw;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAllowsDraw() {
        return allowsDraw;
    }
}