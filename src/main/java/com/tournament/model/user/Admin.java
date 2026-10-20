package com.tournament.model.user;

import com.tournament.model.enums.UserRole;

/**
 * Администратор системы.
 * <p>
 * Обладает максимальными правами: создаёт турниры и управляет
 * учётными записями пользователей.
 */
public class Admin extends User {

    public Admin(String email, String passwordHash, String firstName, String lastName, String phoneNumber) {
        this(null, email, passwordHash, firstName, lastName, phoneNumber);
    }

    public Admin(Long id, String email, String passwordHash, String firstName,
                 String lastName, String phoneNumber) {
        super(id, email, passwordHash, firstName, lastName, phoneNumber, UserRole.ADMIN);
    }

    @Override
    public boolean canCreateTournament() {
        return true;
    }

    @Override
    public boolean canManageUsers() {
        return true;
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
