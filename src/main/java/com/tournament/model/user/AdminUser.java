package com.tournament.model.user;

import com.tournament.model.enums.UserRole;

public class AdminUser extends User {

    public AdminUser() {
        super();
        this.role = UserRole.ADMIN;
    }

    public AdminUser(Long id, String email, String passwordHash, String firstName, String lastName) {
        super(id, email, passwordHash, firstName, lastName, UserRole.ADMIN);
    }

    @Override
    public boolean canCreateTournament() {
        return true;
    }

    @Override
    public boolean canManageUsers() {
        return true;
    }
}