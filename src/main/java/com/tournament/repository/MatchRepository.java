package com.tournament.repository;

import com.tournament.model.match.Match;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс доступа к данным матчей.
 */
public interface MatchRepository {
    Match save(Match match);
    Optional<Match> findById(Long id);
    List<Match> findAll();
    boolean delete(Long id);
}