package com.tournament.model.user;
import com.tournament.model.enums.UserRole;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
/**
 * Пользователь системы, представляющий судью турнира.
 */
@Entity
@DiscriminatorValue("REFEREE")
public class RefereeUser extends User {
    public RefereeUser() {
        setRole(UserRole.REFEREE);
    }
    public RefereeUser(Long id, String email, String passwordHash, String firstName, String lastName) {
        super(id, email, passwordHash, firstName, lastName, UserRole.REFEREE);
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
