package com.tournament.repository;
import com.tournament.model.user.PlayerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
/**
 * Spring Data JPA репозиторий игроков.
 *
 * <p>Вынесен отдельно, так как поля {@code team} и {@code captain} объявлены
 * только в подклассе {@link PlayerUser} (принцип единственной ответственности:
 * запросы по игрокам не «засоряют» общий репозиторий пользователей).</p>
 */
@Repository
public interface PlayerUserRepository extends JpaRepository<PlayerUser, Long> {
    /** Все игроки команды. */
    List<PlayerUser> findByTeamId(Long teamId);
    /** Игроки, являющиеся (или не являющиеся) капитанами. */
    List<PlayerUser> findByCaptain(boolean captain);
    /** Игроки без команды (свободные агенты). */
    List<PlayerUser> findByTeamIsNull();
    /** Поиск игрока по email. */
    Optional<PlayerUser> findByEmailIgnoreCase(String email);
}
