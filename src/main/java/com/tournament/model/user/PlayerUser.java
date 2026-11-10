package com.tournament.model.user;

import com.tournament.model.enums.UserRole;

/**
 * Игрок турнира и пользователь системы.
 * Имеет обычные права базового пользователя системы.
 */
public class PlayerUser extends User {
    private String phoneNumber;
    private boolean captain;

    public PlayerUser() {
        super();
        this.role = UserRole.PLAYER;
    }

    public PlayerUser(Long id, String email, String passwordHash, String firstName, String lastName, String phoneNumber) {
        super(id, email, passwordHash, firstName, lastName, UserRole.PLAYER);
        this.phoneNumber = phoneNumber;
    }

    public boolean isCaptain() {
        return captain;
    }

    public void setCaptain(boolean captain) {
        this.captain = captain;
        this.role = captain ? UserRole.CAPTAIN : UserRole.PLAYER;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean canCreateTournament() {
        return false;
    }

    @Override
    public boolean canManageUsers() {
        return false;
    }
}