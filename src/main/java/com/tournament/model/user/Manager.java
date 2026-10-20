package com.tournament.model.user;

import com.tournament.model.enums.UserRole;

/**
 * Менеджер турниров.
 * <p>
 * Организует соревнования и управляет командами, но не имеет доступа
 * к управлению учётными записями.
 */
public class Manager extends User {

    public Manager(String email, String passwordHash, String firstName, String lastName, String phoneNumber) {
        this(null, email, passwordHash, firstName, lastName, phoneNumber);
    }

    public Manager(Long id, String email, String passwordHash, String firstName,
                   String lastName, String phoneNumber) {
        super(id, email, passwordHash, firstName, lastName, phoneNumber, UserRole.MANAGER);
    }

    @Override
    public boolean canCreateTournament() {
        return true;
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
