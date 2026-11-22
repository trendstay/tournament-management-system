package com.tournament.service;
import com.tournament.exception.AccessDeniedException;
import com.tournament.exception.ConflictException;
import com.tournament.exception.NotFoundException;
import com.tournament.dto.team.TeamFilter;
import com.tournament.dto.team.TeamRequest;
import com.tournament.dto.team.TeamResponse;
import com.tournament.dto.user.UserResponse;
import com.tournament.model.team.Team;
import com.tournament.model.enums.UserRole;
import com.tournament.model.user.CoachUser;
import com.tournament.model.user.PlayerUser;
import com.tournament.support.AbstractRepositoryMockSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * Тесты сервиса команд. База данных не используется — репозитории замоканы.
 */
@DisplayName("TeamService: CRUD, состав команды и фильтры")
class TeamServiceImplTest extends AbstractRepositoryMockSupport {
    @Autowired
    private TeamService teamService;
    private CoachUser coach;
    @BeforeEach
    void setUp() {
        coach = new CoachUser(1L, "coach@test.com", "hash", "Сергей", "Тренеров");
    }
    @Test
    @DisplayName("create: создаёт команду с тренером")
    void create_shouldCreateTeamWithCoach() {
        when(teamRepository.findByNameIgnoreCase("Зенит")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(coach));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> {
            Team team = invocation.getArgument(0);
            team.setId(10L);
            return team;
        });
        TeamResponse response = teamService.create(new TeamRequest("Зенит", "Санкт-Петербург", 1L));
        assertEquals(10L, response.getId());
        assertEquals("Зенит", response.getName());
        assertEquals(1L, response.getCoachId());
    }
    @Test
    @DisplayName("create: пользователь без роли COACH не может быть тренером команды")
    void create_withNonCoachUser_shouldThrowException() {
        PlayerUser player = new PlayerUser(2L, "player@test.com", "hash", "Пётр", "Петров", "+79990000000");
        when(teamRepository.findByNameIgnoreCase("Спартак")).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(player));
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> teamService.create(new TeamRequest("Спартак", "Москва", 2L)));
        assertTrue(exception.getMessage().contains("не является тренером"));
        verify(teamRepository, never()).save(any(Team.class));
    }
    @Test
    @DisplayName("create: дубликат названия команды запрещён")
    void create_withDuplicateName_shouldThrowException() {
        when(teamRepository.findByNameIgnoreCase("Зенит")).thenReturn(Optional.of(new Team(5L, "Зенит", "СПб")));
        assertThrows(ConflictException.class,
                () -> teamService.create(new TeamRequest("Зенит", "Казань", null)));
    }
    @Test
    @DisplayName("addPlayer: игрок добавляется в состав команды")
    void addPlayer_shouldAddPlayerToRoster() {
        Team team = new Team(10L, "Зенит", "Санкт-Петербург");
        PlayerUser player = new PlayerUser(3L, "player@test.com", "hash", "Пётр", "Петров", "+79990000000");
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(playerUserRepository.findById(3L)).thenReturn(Optional.of(player));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TeamResponse response = teamService.addPlayer(10L, 3L);
        assertEquals(1, response.getPlayersCount());
        assertEquals(1, team.getPlayers().size());
        assertEquals(team, player.getTeam());
    }
    @Test
    @DisplayName("addPlayer: переход игрока из другой команды удаляет его из прежнего состава")
    void addPlayer_fromAnotherTeam_shouldMovePlayer() {
        Team oldTeam = new Team(11L, "Спартак", "Москва");
        PlayerUser player = new PlayerUser(3L, "player@test.com", "hash", "Пётр", "Петров", "+79990000000");
        oldTeam.addPlayer(player);
        Team newTeam = new Team(10L, "Зенит", "Санкт-Петербург");
        when(teamRepository.findById(10L)).thenReturn(Optional.of(newTeam));
        when(playerUserRepository.findById(3L)).thenReturn(Optional.of(player));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));
        teamService.addPlayer(10L, 3L);
        assertEquals(0, oldTeam.getPlayers().size());
        assertEquals(1, newTeam.getPlayers().size());
    }
    @Test
    @DisplayName("appointCaptain: назначает капитана и меняет роль игрока")
    void appointCaptain_shouldSetCaptain() {
        Team team = new Team(10L, "Зенит", "Санкт-Петербург");
        PlayerUser player = new PlayerUser(3L, "player@test.com", "hash", "Пётр", "Петров", "+79990000000");
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(playerUserRepository.findById(3L)).thenReturn(Optional.of(player));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TeamResponse response = teamService.appointCaptain(10L, 3L);
        assertEquals(3L, response.getCaptainId());
        assertTrue(player.isCaptain());
        assertEquals(UserRole.CAPTAIN, player.getRole());
    }
    @Test
    @DisplayName("removePlayer: игрок удаляется из состава")
    void removePlayer_shouldRemoveFromRoster() {
        Team team = new Team(10L, "Зенит", "Санкт-Петербург");
        PlayerUser player = new PlayerUser(3L, "player@test.com", "hash", "Пётр", "Петров", "+79990000000");
        team.addPlayer(player);
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(playerUserRepository.findById(3L)).thenReturn(Optional.of(player));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TeamResponse response = teamService.removePlayer(10L, 3L);
        assertEquals(0, response.getPlayersCount());
        assertNull(player.getTeam());
    }
    @Test
    @DisplayName("delete: команду с игроками удалить нельзя")
    void delete_teamWithPlayers_shouldThrowException() {
        Team team = new Team(10L, "Зенит", "Санкт-Петербург");
        team.addPlayer(new PlayerUser(3L, "player@test.com", "hash", "Пётр", "Петров", "+79990000000"));
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        assertThrows(ConflictException.class, () -> teamService.delete(10L));
        verify(teamRepository, never()).delete(any(Team.class));
    }
    @Test
    @DisplayName("delete: пустую команду удаляем")
    void delete_emptyTeam_shouldRemoveTeam() {
        Team team = new Team(10L, "Зенит", "Санкт-Петербург");
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(matchRepository.existsByHomeTeamIdOrAwayTeamId(10L, 10L)).thenReturn(false);
        teamService.delete(10L);
        verify(teamRepository).delete(team);
    }
    @Test
    @DisplayName("findPlayers: возвращает состав команды")
    void findPlayers_shouldReturnRoster() {
        Team team = new Team(10L, "Зенит", "Санкт-Петербург");
        team.addPlayer(new PlayerUser(3L, "player@test.com", "hash", "Пётр", "Петров", "+79990000000"));
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        List<UserResponse> players = teamService.findPlayers(10L);
        assertEquals(1, players.size());
        assertEquals("player@test.com", players.get(0).getEmail());
    }
    @Test
    @DisplayName("findByFilter: поиск по городу")
    void findByFilter_byCity_shouldUseCityQuery() {
        when(teamRepository.findByCityIgnoreCase("Москва"))
                .thenReturn(List.of(new Team(2L, "Спартак", "Москва")));
        TeamFilter filter = new TeamFilter();
        filter.setCity("Москва");
        List<TeamResponse> teams = teamService.findByFilter(filter);
        assertEquals(1, teams.size());
        assertEquals("Спартак", teams.get(0).getName());
    }
    @Test
    @DisplayName("findById: отсутствующая команда приводит к NotFoundException")
    void findById_whenMissing_shouldThrowException() {
        when(teamRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> teamService.findById(404L));
    }
}
