package com.tournament.repository;
import com.tournament.model.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
/**
 * Spring Data JPA репозиторий команд.
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    /** Фильтр по городу. */
    List<Team> findByCityIgnoreCase(String city);
    /** Фильтр по части названия команды. */
    List<Team> findByNameContainingIgnoreCase(String name);
    /** Команды конкретного тренера. */
    List<Team> findByCoachId(Long coachId);
    /** Проверка уникальности названия команды. */
    boolean existsByNameIgnoreCase(String name);
    /** Поиск команды по названию (без учёта регистра). */
    Optional<Team> findByNameIgnoreCase(String name);
    /** Команда, капитаном которой является игрок. */
    Optional<Team> findByCaptainId(Long captainId);
}
