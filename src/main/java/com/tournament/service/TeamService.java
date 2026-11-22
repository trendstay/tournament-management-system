package com.tournament.service;
import com.tournament.dto.team.TeamFilter;
import com.tournament.dto.team.TeamRequest;
import com.tournament.dto.team.TeamResponse;
import com.tournament.dto.user.UserResponse;
import java.util.List;
/**
 * Сервис управления командами (CRUD + состав команды + фильтры).
 */
public interface TeamService extends CrudService<TeamRequest, TeamResponse> {
    /** Выборка команд по фильтру (город, название, тренер). */
    List<TeamResponse> findByFilter(TeamFilter filter);
    /** Добавить игрока в команду. */
    TeamResponse addPlayer(Long teamId, Long playerId);
    /** Удалить игрока из команды. */
    TeamResponse removePlayer(Long teamId, Long playerId);
    /** Назначить капитана команды. */
    TeamResponse appointCaptain(Long teamId, Long playerId);
    /** Состав команды (список игроков). */
    List<UserResponse> findPlayers(Long teamId);
}
