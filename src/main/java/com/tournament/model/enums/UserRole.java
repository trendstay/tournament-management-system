package com.tournament.model.enums;

/**
 * Роли пользователей системы.
 * <p>
 * Роль определяет, какие действия доступны пользователю: организация турниров,
 * управление пользователями, управление составом команды или судейство матчей.
 */
public enum UserRole {
    ADMIN("Администратор"),
    MANAGER("Менеджер турниров"),
    PLAYER("Игрок"),
    CAPTAIN("Капитан команды"),
    COACH("Тренер команды"),
    REFEREE("Судья");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
