package com.tournament.repository;

import com.tournament.model.enums.SportType;
import com.tournament.model.tournament.Tournament;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * In-memory реализация репозитория турниров.
 */
public class InMemoryTournamentRepository extends AbstractInMemoryRepository<Tournament>
        implements TournamentRepository {

    @Override
    public List<Tournament> findByTitle(String title) {
        if (title == null) {
            return List.of();
        }
        return filter(tournament -> tournament.getTitle().equalsIgnoreCase(title));
    }

    @Override
    public List<Tournament> findBySportType(SportType sportType) {
        if (sportType == null) {
            return List.of();
        }
        return filter(tournament -> tournament.getSportType() == sportType);
    }

    @Override
    public Optional<Tournament> findByTitleAndStartDate(String title, LocalDate startDate) {
        if (title == null || startDate == null) {
            return Optional.empty();
        }
        return findFirst(tournament -> tournament.getTitle().equalsIgnoreCase(title)
                && Objects.equals(tournament.getStartDate(), startDate));
    }
}
