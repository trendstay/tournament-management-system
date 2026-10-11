package com.tournament.model.user;

import com.tournament.model.enums.UserRole;

public class CustomerUser extends User {
    private String phoneNumber;

    public CustomerUser() {
        super();
        this.role = UserRole.PLAYER;
    }

    public CustomerUser(Long id, String email, String passwordHash, String firstName, String lastName, String phoneNumber) {
        super(id, email, passwordHash, firstName, lastName, UserRole.PLAYER);
        this.phoneNumber = phoneNumber;
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