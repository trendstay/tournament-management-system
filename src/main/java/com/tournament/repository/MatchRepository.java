package com.tournament.repository;
import com.tournament.model.enums.MatchStatus;
import com.tournament.model.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
/**
 * Spring Data JPA репозиторий матчей.
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    /** Фильтр по статусу матча. */
    List<Match> findByStatus(MatchStatus status);
    /** Все матчи турнира. */
    List<Match> findByTournamentId(Long tournamentId);
    /** Фильтр по дате проведения. */
    List<Match> findByMatchDateBetween(LocalDateTime from, LocalDateTime to);
    /**
     * Матчи с участием команды (в роли хозяев или гостей).
     * Используется явный JPQL-запрос, так как условие содержит {@code OR}.
     */
    @Query("select m from Match m where m.homeTeam.id = :teamId or m.awayTeam.id = :teamId")
    List<Match> findByTeamId(@Param("teamId") Long teamId);
    /** Количество матчей в турнире. */
    long countByTournamentId(Long tournamentId);
    /** Есть ли у команды матчи (проверка перед удалением команды). */
    boolean existsByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);
}
