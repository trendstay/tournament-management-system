package com.tournament.repository;
import com.tournament.model.enums.SportType;
import com.tournament.model.tournament.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
/**
 * Spring Data JPA репозиторий турниров.
 */
@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    /** Фильтр по виду спорта. */
    List<Tournament> findBySportType(SportType sportType);
    /** Фильтр по части названия турнира. */
    List<Tournament> findByTitleContainingIgnoreCase(String title);
    /** Фильтр по дате начала (включительно). */
    List<Tournament> findByStartDateBetween(LocalDate from, LocalDate to);
    /** Турниры, которые судит конкретный судья. */
    List<Tournament> findByRefereeId(Long refereeId);
    /** Сколько турниров содержит команда. */
    long countByTeamsId(Long teamId);
}
