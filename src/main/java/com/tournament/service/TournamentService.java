package com.tournament.service;

import com.tournament.model.enums.SportType;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import com.tournament.model.user.Referee;
import com.tournament.repository.TournamentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис управления турнирами: создание, назначение судьи, регистрация команд
 * и формирование турнирной таблицы.
 */
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = Objects.requireNonNull(
                tournamentRepository, "Репозиторий турниров не может быть null");
    }

    /**
     * Создаёт турнир. Пара «название + дата начала» должна быть уникальной,
     * как составной первичный ключ в ER-модели.
     */
    public Tournament createTournament(String title, SportType sportType, LocalDate startDate,
                                       LocalDate endDate, BigDecimal prizePool) {
        if (title != null && startDate != null
                && tournamentRepository.findByTitleAndStartDate(title, startDate).isPresent()) {
            throw new IllegalArgumentException(
                    "Турнир '" + title + "' с датой начала " + startDate + " уже существует");
        }
        Tournament tournament = new Tournament(null, title, sportType, startDate, endDate, prizePool);
        tournamentRepository.save(tournament);
        return tournament;
    }

    /**
     * Назначает турниру главного судью.
     */
    public Tournament assignReferee(Long tournamentId, Referee referee) {
        Tournament tournament = getTournament(tournamentId);
        tournament.setReferee(Objects.requireNonNull(referee, "Судья не может быть null"));
        return tournamentRepository.save(tournament);
    }

    /**
     * Регистрирует команду на турнир.
     */
    public Tournament registerTeam(Long tournamentId, Team team) {
        Tournament tournament = getTournament(tournamentId);
        tournament.addTeam(team);
        return tournamentRepository.save(tournament);
    }

    /**
     * Снимает команду с турнира.
     */
    public Tournament removeTeam(Long tournamentId, Team team) {
        Tournament tournament = getTournament(tournamentId);
        tournament.removeTeam(team);
        return tournamentRepository.save(tournament);
    }

    /**
     * Находит турнир по идентификатору.
     *
     * @throws IllegalArgumentException если турнир не найден
     */
    public Tournament findById(Long id) {
        return getTournament(id);
    }

    public List<Tournament> findAll() {
        return tournamentRepository.findAll();
    }

    public List<Tournament> findByTitle(String title) {
        return tournamentRepository.findByTitle(title);
    }

    public List<Tournament> findBySportType(SportType sportType) {
        return tournamentRepository.findBySportType(sportType);
    }

    public Optional<Tournament> findByTitleAndStartDate(String title, LocalDate startDate) {
        return tournamentRepository.findByTitleAndStartDate(title, startDate);
    }

    /**
     * Строит турнирную таблицу для указанного турнира.
     */
    public String buildStandings(Long tournamentId) {
        return getTournament(tournamentId).generateStandingsTable();
    }

    public boolean deleteTournament(Long id) {
        return tournamentRepository.deleteById(id);
    }

    private Tournament getTournament(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Турнир с ID " + id + " не найден"));
    }
}
