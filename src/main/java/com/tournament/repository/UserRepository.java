package com.tournament.repository;

import com.tournament.model.enums.UserRole;
import com.tournament.model.user.User;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий пользователей с поиском по уникальному email, роли и фамилии.
 */
public interface UserRepository extends BaseRepository<User> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(UserRole role);

    List<User> findByLastName(String lastName);
}
