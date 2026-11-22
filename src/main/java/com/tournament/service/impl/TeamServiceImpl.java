package com.tournament.service.impl;
import com.tournament.exception.AccessDeniedException;
import com.tournament.exception.ConflictException;
import com.tournament.exception.NotFoundException;
import com.tournament.exception.ValidationException;
import com.tournament.dto.team.TeamFilter;
import com.tournament.dto.team.TeamRequest;
import com.tournament.dto.team.TeamResponse;
import com.tournament.dto.user.UserResponse;
import com.tournament.mapper.TeamMapper;
import com.tournament.mapper.UserMapper;
import com.tournament.model.team.Team;
import com.tournament.model.user.CoachUser;
import com.tournament.model.user.PlayerUser;
import com.tournament.model.user.User;
import com.tournament.repository.MatchRepository;
import com.tournament.repository.PlayerUserRepository;
import com.tournament.repository.TeamRepository;
import com.tournament.repository.UserRepository;
import com.tournament.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * Реализация сервиса команд: CRUD, управление составом и капитаном, фильтры.
 */
@Service
@Transactional
public class TeamServiceImpl extends AbstractCrudService<Team, TeamRequest, TeamResponse> implements TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PlayerUserRepository playerUserRepository;
    private final MatchRepository matchRepository;
    private final TeamMapper teamMapper;
    private final UserMapper userMapper;
    public TeamServiceImpl(TeamRepository teamRepository,
                           UserRepository userRepository,
                           PlayerUserRepository playerUserRepository,
                           MatchRepository matchRepository,
                           TeamMapper teamMapper,
                           UserMapper userMapper) {
        super(teamRepository, teamMapper);
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.playerUserRepository = playerUserRepository;
        this.matchRepository = matchRepository;
        this.teamMapper = teamMapper;
        this.userMapper = userMapper;
    }
    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> findByFilter(TeamFilter filter) {
        if (filter == null || filter.isEmpty()) {
            return toResponseList(teamRepository.findAll());
        }
        if (filter.getCity() != null && !filter.getCity().isBlank()) {
            return teamMapper.toResponseList(teamRepository.findByCityIgnoreCase(filter.getCity()));
        }
        if (filter.getName() != null && !filter.getName().isBlank()) {
            return teamMapper.toResponseList(teamRepository.findByNameContainingIgnoreCase(filter.getName()));
        }
        return teamMapper.toResponseList(teamRepository.findByCoachId(filter.getCoachId()));
    }
    @Override
    public TeamResponse addPlayer(Long teamId, Long playerId) {
        Team team = findEntity(teamId);
        PlayerUser player = findPlayer(playerId);
        Team previousTeam = player.getTeam();
        if (previousTeam != null && !previousTeam.equals(team)) {
            previousTeam.removePlayer(player);
        }
        team.addPlayer(player);
        return toResponse(teamRepository.save(team));
    }
    @Override
    public TeamResponse removePlayer(Long teamId, Long playerId) {
        Team team = findEntity(teamId);
        PlayerUser player = findPlayer(playerId);
        team.removePlayer(player);
        return toResponse(teamRepository.save(team));
    }
    @Override
    public TeamResponse appointCaptain(Long teamId, Long playerId) {
        Team team = findEntity(teamId);
        PlayerUser player = findPlayer(playerId);
        if (!team.getPlayers().contains(player)) {
            team.addPlayer(player);
        }
        team.setCaptain(player);
        return toResponse(teamRepository.save(team));
    }
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findPlayers(Long teamId) {
        Team team = findEntity(teamId);
        List<User> players = new ArrayList<>(team.getPlayers());
        return userMapper.toResponseList(players);
    }
    @Override
    public void delete(Long id) {
        Team team = findEntity(id);
        if (!team.getPlayers().isEmpty()) {
            throw new ConflictException(
                    "Нельзя удалить команду с ID " + id + ": в составе есть игроки",
                    "Сначала уберите всех игроков из состава команды, затем повторите удаление",
                    "TEAM_HAS_PLAYERS");
        }
        if (matchRepository.existsByHomeTeamIdOrAwayTeamId(id, id)) {
            throw new ConflictException(
                    "Нельзя удалить команду с ID " + id + ": у неё есть матчи",
                    "Сначала удалите или перенесите матчи команды, затем повторите удаление",
                    "TEAM_HAS_MATCHES");
        }
        teamRepository.delete(team);
    }
    @Override
    protected Team createEntity(TeamRequest request) {
        validateNameUnique(request.getName(), null);
        Team team = new Team(null, request.getName(), request.getCity());
        team.setCoach(findCoach(request.getCoachId()));
        return team;
    }
    @Override
    protected void applyRequest(Team entity, TeamRequest request) {
        validateNameUnique(request.getName(), entity.getId());
        entity.setName(request.getName());
        entity.setCity(request.getCity());
        entity.setCoach(findCoach(request.getCoachId()));
    }
    @Override
    protected String entityName() {
        return "Команда";
    }
    /** Название команды должно быть уникальным (без учёта регистра). */
    private void validateNameUnique(String name, Long currentTeamId) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Название команды не может быть пустым",
                    "Укажите непустое название команды");
        }
        Optional<Team> existingTeam = teamRepository.findByNameIgnoreCase(name);
        boolean nameTaken = existingTeam
                .filter(team -> currentTeamId == null || !currentTeamId.equals(team.getId()))
                .isPresent();
        if (nameTaken) {
            throw new ConflictException(
                    "Команда с названием «" + name + "» уже существует",
                    "Укажите другое название команды или откройте существующую команду «" + name + "» и измените её",
                    "TEAM_NAME_EXISTS");
        }
    }
    /** Найти тренера по идентификатору (необязательное поле). */
    private CoachUser findCoach(Long coachId) {
        if (coachId == null) {
            return null;
        }
        User user = userRepository.findById(coachId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + coachId + " не найден"));
        if (!(user instanceof CoachUser)) {
            throw new AccessDeniedException("создание команды",
                    "пользователь с ID " + coachId + " не является тренером",
                    "Назначьте пользователя с ролью COACH или выберите другого тренера");
        }
        return (CoachUser) user;
    }
    private PlayerUser findPlayer(Long playerId) {
        return playerUserRepository.findById(playerId)
                .orElseThrow(() -> new NotFoundException("Игрок с ID " + playerId + " не найден"));
    }
}
