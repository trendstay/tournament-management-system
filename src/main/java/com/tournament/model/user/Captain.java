package com.tournament.model.user;

import com.tournament.model.enums.UserRole;

/**
 * Капитан команды.
 * <p>
 * Капитан — это игрок, который дополнительно может управлять составом
 * команды и представлять её на турнирах. Наследование от {@link Player}
 * подчёркивает, что капитан всегда является членом команды.
 */
public class Captain extends Player {

    public Captain(String email, String passwordHash, String firstName, String lastName, String phoneNumber) {
        this(null, email, passwordHash, firstName, lastName, phoneNumber);
    }

    public Captain(Long id, String email, String passwordHash, String firstName,
                   String lastName, String phoneNumber) {
        super(id, UserRole.CAPTAIN, email, passwordHash, firstName, lastName, phoneNumber);
    }

    @Override
    public boolean canManageTeam() {
        return true;
    }
}
