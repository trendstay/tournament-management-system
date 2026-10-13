package com.tournament.model.user;

import com.tournament.model.enums.UserRole;

/**
 * Класс пользователя с ролью "Менеджер".
 * Может создавать турниры, но не может управлять учетными записями других пользователей.
 */
public class ManagerUser extends User {

    public ManagerUser() {
        super();
        this.role = UserRole.MANAGER;
    }

    public ManagerUser(Long id, String email, String passwordHash, String firstName, String lastName) {
        super(id, email, passwordHash, firstName, lastName, UserRole.MANAGER);
    }

    @Override
    public boolean canCreateTournament() {
        return true;
    }

    @Override
    public boolean canManageUsers() {
        return false;
    }
}