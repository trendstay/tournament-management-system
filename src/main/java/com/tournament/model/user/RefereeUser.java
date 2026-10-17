package com.tournament.model.user;

import com.tournament.model.enums.UserRole;

/** Пользователь системы, представляющий судью турнира. */
public class RefereeUser extends User {
    public RefereeUser() { this.role = UserRole.REFEREE; }
    public RefereeUser(Long id, String email, String passwordHash, String firstName, String lastName) {
        super(id, email, passwordHash, firstName, lastName, UserRole.REFEREE);
    }
    @Override public boolean canCreateTournament() { return false; }
    @Override public boolean canManageUsers() { return false; }
}
