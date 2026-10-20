package com.tournament.repository;

import com.tournament.model.team.Team;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий команд с поиском по названию и городу.
 */
public interface TeamRepository extends BaseRepository<Team> {

    Optional<Team> findByName(String name);

    List<Team> findByCity(String city);
}
