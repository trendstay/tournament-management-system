package com.tournament.model.user;
import com.tournament.model.enums.UserRole;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
/**
 * Класс пользователя с ролью "Администратор".
 * Имеет максимальные привилегии в системе.
 */
@Entity
@DiscriminatorValue("ADMIN")
public class AdminUser extends User {
    public AdminUser() {
        super();
        setRole(UserRole.ADMIN);
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
