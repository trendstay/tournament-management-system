package com.tournament.mapper;
import com.tournament.dto.match.MatchResponse;
import com.tournament.model.match.Match;
import com.tournament.model.team.Team;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
/**
 * Маппер матчей: сущность {@link Match} → {@link MatchResponse}.
 */
@Component
public class MatchMapper implements DtoMapper<Match, MatchResponse> {
    @Override
    public MatchResponse toResponse(Match entity) {
        MatchResponse response = new MatchResponse();
        response.setId(entity.getId());
        response.setScore(entity.getScore());
        response.setMatchDate(entity.getMatchDate());
        if (entity.getStatus() != null) {
            response.setStatus(entity.getStatus().name());
        }
        if (entity.getHomeTeam() != null) {
            response.setHomeTeamId(entity.getHomeTeam().getId());
            response.setHomeTeamName(entity.getHomeTeam().getName());
        }
        if (entity.getAwayTeam() != null) {
            response.setAwayTeamId(entity.getAwayTeam().getId());
            response.setAwayTeamName(entity.getAwayTeam().getName());
        }
        if (entity.getTournament() != null) {
            response.setTournamentId(entity.getTournament().getId());
            response.setTournamentTitle(entity.getTournament().getTitle());
        }
        Team winner = entity.getWinner();
        if (winner != null) {
            response.setWinnerTeamName(winner.getName());
        }
        response.setMatchReport(entity.generateMatchReport());
        return response;
    }
    /** Преобразовать список сущностей в список DTO. */
    public List<MatchResponse> toResponseList(List<Match> entities) {
        List<MatchResponse> result = new ArrayList<>();
        for (Match entity : entities) {
            result.add(toResponse(entity));
        }
        return result;
    }
}
