package com.tournament.mapper;
import com.tournament.dto.tournament.TournamentResponse;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
/**
 * Маппер турниров: сущность {@link Tournament} → {@link TournamentResponse}.
 */
@Component
public class TournamentMapper implements DtoMapper<Tournament, TournamentResponse> {
    @Override
    public TournamentResponse toResponse(Tournament entity) {
        TournamentResponse response = new TournamentResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setStartDate(entity.getStartDate());
        response.setEndDate(entity.getEndDate());
        response.setPrizePool(entity.getPrizePool());
        if (entity.getSportType() != null) {
            response.setSportType(entity.getSportType().name());
            response.setSportDisplayName(entity.getSportType().getDisplayName());
        }
        if (entity.getReferee() != null) {
            response.setRefereeId(entity.getReferee().getId());
            response.setRefereeFullName(buildFullName(
                    entity.getReferee().getFirstName(), entity.getReferee().getLastName()));
        }
        response.setRegistrationOpen(entity.isRegistrationOpen());
        response.setTeamsCount(entity.getTeams().size());
        response.setMatchesCount(entity.getMatches().size());
        for (Team team : entity.getTeams()) {
            response.getTeamIds().add(team.getId());
        }
        response.setStandingsTable(entity.generateStandingsTable());
        return response;
    }
    /** Преобразовать список сущностей в список DTO. */
    public List<TournamentResponse> toResponseList(List<Tournament> entities) {
        List<TournamentResponse> result = new ArrayList<>();
        for (Tournament entity : entities) {
            result.add(toResponse(entity));
        }
        return result;
    }
    private String buildFullName(String firstName, String lastName) {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
    }
}
