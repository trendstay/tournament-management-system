package com.tournament.repository;
import com.tournament.model.enums.UserRole;
import com.tournament.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
/**
 * Spring Data JPA репозиторий пользователей.
 *
 * <p>Наследование от {@link JpaRepository} даёт готовые реализации всех операций CRUD
 * (save, findById, findAll, delete и т.д.). Дополнительные методы-фильтры строятся
 * автоматически по имени метода (derived queries).</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /** Поиск пользователя по email без учёта регистра. */
    Optional<User> findByEmailIgnoreCase(String email);
    /** Проверка существования пользователя с таким email. */
    boolean existsByEmailIgnoreCase(String email);
    /** Фильтр по роли. */
    List<User> findByRole(UserRole role);
    /** Фильтр по роли и части фамилии. */
    List<User> findByRoleAndLastNameContainingIgnoreCase(UserRole role, String lastName);
    /** Фильтр по части фамилии. */
    List<User> findByLastNameContainingIgnoreCase(String lastName);
}
