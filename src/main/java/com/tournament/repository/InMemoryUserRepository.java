package com.tournament.repository;

import com.tournament.model.enums.UserRole;
import com.tournament.model.user.User;

import java.util.List;
import java.util.Optional;

/**
 * In-memory реализация репозитория пользователей.
 */
public class InMemoryUserRepository extends AbstractInMemoryRepository<User> implements UserRepository {

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return findFirst(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public List<User> findByRole(UserRole role) {
        if (role == null) {
            return List.of();
        }
        return filter(user -> user.getRole() == role);
    }

    @Override
    public List<User> findByLastName(String lastName) {
        if (lastName == null) {
            return List.of();
        }
        return filter(user -> user.getLastName() != null
                && user.getLastName().equalsIgnoreCase(lastName));
    }
}
