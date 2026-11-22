package com.tournament.service.factory;
import com.tournament.exception.ValidationException;
import com.tournament.model.enums.UserRole;
import com.tournament.model.user.AdminUser;
import com.tournament.model.user.CoachUser;
import com.tournament.model.user.ManagerUser;
import com.tournament.model.user.PlayerUser;
import com.tournament.model.user.RefereeUser;
import com.tournament.model.user.User;
import org.springframework.stereotype.Component;
/**
 * Фабрика пользователей (паттерн «Фабричный метод»).
 * Отдельный класс (SRP): создание объектов иерархии {@link User} по роли
 * вынесено из сервиса.
 */
@Component
public class UserFactory {
    /** Создать пользователя нужного подкласса по роли. */
    public User create(UserRole role,
                       String email,
                       String passwordHash,
                       String firstName,
                       String lastName) {
        if (role == null) {
            throw new ValidationException("Роль пользователя обязательна для создания учётной записи",
                    "Укажите одну из поддерживаемых ролей пользователя");
        }
        switch (role) {
            case ADMIN:
                return new AdminUser(null, email, passwordHash, firstName, lastName);
            case MANAGER:
                return new ManagerUser(null, email, passwordHash, firstName, lastName);
            case COACH:
                return new CoachUser(null, email, passwordHash, firstName, lastName);
            case REFEREE:
                return new RefereeUser(null, email, passwordHash, firstName, lastName);
            case PLAYER:
            case CAPTAIN:
                return new PlayerUser(null, email, passwordHash, firstName, lastName, null);
            default:
                throw new ValidationException("Неподдерживаемая роль пользователя: " + role,
                        "Выберите роль ADMIN, MANAGER, COACH, REFEREE, PLAYER или CAPTAIN");
        }
    }
}
