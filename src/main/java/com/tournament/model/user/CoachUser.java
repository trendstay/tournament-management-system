package com.tournament.model.user;
import com.tournament.model.enums.UserRole;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
/**
 * Пользователь системы, представляющий тренера команды.
 */
@Entity
@DiscriminatorValue("COACH")
public class CoachUser extends User {
    public CoachUser() {
        setRole(UserRole.COACH);
    }
    public CoachUser(Long id, String email, String passwordHash, String firstName, String lastName) {
        super(id, email, passwordHash, firstName, lastName, UserRole.COACH);
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
