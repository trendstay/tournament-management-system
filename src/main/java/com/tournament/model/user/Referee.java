package com.tournament.model.user;

import com.tournament.model.enums.UserRole;

/**
 * Судья.
 * <p>
 * Обслуживает матчи турнира. Каждый турнир имеет главного судью
 * ({@code Tournament#getReferee()}), а каждый матч — назначенного судью.
 */
public class Referee extends User {

    public Referee(String email, String passwordHash, String firstName, String lastName, String phoneNumber) {
        this(null, email, passwordHash, firstName, lastName, phoneNumber);
    }

    public Referee(Long id, String email, String passwordHash, String firstName,
                   String lastName, String phoneNumber) {
        super(id, email, passwordHash, firstName, lastName, phoneNumber, UserRole.REFEREE);
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
        return false;
    }

    @Override
    public boolean canRefereeMatches() {
        return true;
    }
}
