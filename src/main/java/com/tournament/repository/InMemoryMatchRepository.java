package com.tournament.repository;

import com.tournament.model.enums.MatchStatus;
import com.tournament.model.match.Match;

import java.util.List;

/**
 * In-memory реализация репозитория матчей.
 */
public class InMemoryMatchRepository extends AbstractInMemoryRepository<Match> implements MatchRepository {

    @Override
    public List<Match> findByStatus(MatchStatus status) {
        if (status == null) {
            return List.of();
        }
        return filter(match -> match.getStatus() == status);
    }

    @Override
    public List<Match> findByTournamentId(Long tournamentId) {
        if (tournamentId == null) {
            return List.of();
        }
        return filter(match -> match.getTournament() != null
                && tournamentId.equals(match.getTournament().getId()));
    }

    @Override
    public List<Match> findByRefereeId(Long refereeId) {
        if (refereeId == null) {
            return List.of();
        }
        return filter(match -> match.getReferee() != null
                && refereeId.equals(match.getReferee().getId()));
    }
}
