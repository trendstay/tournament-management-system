package com.tournament.repository;

import com.tournament.model.team.Team;

import java.util.List;
import java.util.Optional;

/**
 * In-memory реализация репозитория команд.
 */
public class InMemoryTeamRepository extends AbstractInMemoryRepository<Team> implements TeamRepository {

    @Override
    public Optional<Team> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return findFirst(team -> team.getName().equalsIgnoreCase(name));
    }

    @Override
    public List<Team> findByCity(String city) {
        if (city == null) {
            return List.of();
        }
        return filter(team -> team.getCity() != null && team.getCity().equalsIgnoreCase(city));
    }
}
