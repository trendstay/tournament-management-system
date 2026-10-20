package com.tournament.model.user;

import com.tournament.model.enums.UserRole;

/**
 * Игрок — участник команды.
 * <p>
 * Игроки хранятся в составе команды ({@code Team#getPlayers()}).
 * Капитан команды является игроком с расширенными правами и представлен
 * отдельным классом {@link Captain}.
 */
public class Player extends User {

    public Player(String email, String passwordHash, String firstName, String lastName, String phoneNumber) {
        this(null, UserRole.PLAYER, email, passwordHash, firstName, lastName, phoneNumber);
    }

    public Player(Long id, String email, String passwordHash, String firstName,
                  String lastName, String phoneNumber) {
        this(id, UserRole.PLAYER, email, passwordHash, firstName, lastName, phoneNumber);
    }

    protected Player(Long id, UserRole role, String email, String passwordHash,
                     String firstName, String lastName, String phoneNumber) {
        super(id, email, passwordHash, firstName, lastName, phoneNumber, role);
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
        return false;
    }
}
