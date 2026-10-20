package com.tournament.repository;

import com.tournament.model.enums.MatchStatus;
import com.tournament.model.match.Match;

import java.util.List;

/**
 * Репозиторий матчей с поиском по статусу, турниру и судье.
 */
public interface MatchRepository extends BaseRepository<Match> {

    List<Match> findByStatus(MatchStatus status);

    List<Match> findByTournamentId(Long tournamentId);

    List<Match> findByRefereeId(Long refereeId);
}
