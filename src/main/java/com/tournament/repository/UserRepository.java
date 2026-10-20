package com.tournament.repository;

import com.tournament.model.user.User;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс доступа к данным пользователей.
 * Реализует паттерн Repository для соблюдения принципа единственной ответственности (SRP).
 */
public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    boolean delete(Long id);
}