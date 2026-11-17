package com.tournament.mapper;
import com.tournament.dto.team.TeamResponse;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import com.tournament.model.user.PlayerUser;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
/**
 * Маппер команд: сущность {@link Team} → {@link TeamResponse}.
 */
@Component
public class TeamMapper implements DtoMapper<Team, TeamResponse> {
    @Override
    public TeamResponse toResponse(Team entity) {
        TeamResponse response = new TeamResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setCity(entity.getCity());
        if (entity.getCoach() != null) {
            response.setCoachId(entity.getCoach().getId());
            response.setCoachFullName(buildFullName(entity.getCoach().getFirstName(), entity.getCoach().getLastName()));
        }
        if (entity.getCaptain() != null) {
            response.setCaptainId(entity.getCaptain().getId());
            response.setCaptainFullName(buildFullName(entity.getCaptain().getFirstName(), entity.getCaptain().getLastName()));
        }
        List<PlayerUser> players = entity.getPlayers();
        response.setPlayersCount(players.size());
        for (PlayerUser player : players) {
            response.getPlayerIds().add(player.getId());
        }
        List<Tournament> tournaments = entity.getTournaments();
        for (Tournament tournament : tournaments) {
            response.getTournamentIds().add(tournament.getId());
        }
        return response;
    }
    /** Преобразовать список сущностей в список DTO. */
    public List<TeamResponse> toResponseList(List<Team> entities) {
        List<TeamResponse> result = new ArrayList<>();
        for (Team entity : entities) {
            result.add(toResponse(entity));
        }
        return result;
    }
    private String buildFullName(String firstName, String lastName) {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
    }
}
