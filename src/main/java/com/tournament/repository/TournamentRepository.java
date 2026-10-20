package com.tournament.repository;

import com.tournament.model.enums.SportType;
import com.tournament.model.tournament.Tournament;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий турниров с поиском по названию, виду спорта и составному ключу
 * «название + дата начала» (первичный ключ из ER-модели).
 */
public interface TournamentRepository extends BaseRepository<Tournament> {

    List<Tournament> findByTitle(String title);

    List<Tournament> findBySportType(SportType sportType);

    Optional<Tournament> findByTitleAndStartDate(String title, LocalDate startDate);
}
