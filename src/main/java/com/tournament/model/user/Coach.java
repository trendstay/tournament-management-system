package com.tournament.model.user;

import com.tournament.model.enums.UserRole;

/**
 * Тренер команды.
 * <p>
 * Отвечает за подготовку команды и управление её составом.
 * Каждая команда может быть связана с тренером ({@code Team#getCoach()}).
 */
public class Coach extends User {

    public Coach(String email, String passwordHash, String firstName, String lastName, String phoneNumber) {
        this(null, email, passwordHash, firstName, lastName, phoneNumber);
    }

    public Coach(Long id, String email, String passwordHash, String firstName,
                 String lastName, String phoneNumber) {
        super(id, email, passwordHash, firstName, lastName, phoneNumber, UserRole.COACH);
    }

    @Override
    public boolean canCreateTournament() {
        return false;
    }

    @Override
    public boolean canManageUsers() {
        return false;
    }

    @Override
    public boolean canManageTeam() {
        return true;
    }

    @Override
    public boolean canRefereeMatches() {
        return false;
    }
}
