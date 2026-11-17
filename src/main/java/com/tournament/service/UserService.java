package com.tournament.service;
import com.tournament.dto.user.UserFilter;
import com.tournament.dto.user.UserRequest;
import com.tournament.dto.user.UserResponse;
import com.tournament.model.enums.UserRole;
import java.util.List;
/**
 * Сервис управления пользователями (CRUD + фильтры).
 */
public interface UserService extends CrudService<UserRequest, UserResponse> {
    /** Выборка пользователей по фильтру (роль и/или часть фамилии). */
    List<UserResponse> findByFilter(UserFilter filter);
    /** Все пользователи с указанной ролью. */
    List<UserResponse> findAllByRole(UserRole role);
    /** Поиск пользователя по email. */
    UserResponse findByEmail(String email);
}
