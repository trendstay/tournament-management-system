package com.tournament.service;
import com.tournament.dto.tournament.TournamentFilter;
import com.tournament.dto.tournament.TournamentRequest;
import com.tournament.dto.tournament.TournamentResponse;
import com.tournament.model.enums.SportType;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import com.tournament.model.user.CoachUser;
import com.tournament.model.user.RefereeUser;
import com.tournament.support.AbstractRepositoryMockSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * Тесты сервиса турниров. База данных не используется — репозитории замоканы.
 */
@DisplayName("TournamentService: CRUD, регистрация команд и фильтры")
class TournamentServiceImplTest extends AbstractRepositoryMockSupport {
    @Autowired
    private TournamentService tournamentService;
    private RefereeUser referee;
    private Team firstTeam;
    private Team secondTeam;
    @BeforeEach
    void setUp() {
        referee = new RefereeUser(1L, "referee@test.com", "hash", "Николай", "Судьин");
        firstTeam = new Team(1L, "Зенит", "Санкт-Петербург");
        secondTeam = new Team(2L, "Спартак", "Москва");
    }
    @Test
    @DisplayName("create: создаёт турнир с судьёй, командами и призовым фондом")
    void create_shouldCreateTournamentWithTeamsAndReferee() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(referee));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(firstTeam));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(secondTeam));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> {
            Tournament tournament = invocation.getArgument(0);
            tournament.setId(100L);
            return tournament;
        });
        TournamentRequest request = new TournamentRequest("Осенний Кубок", SportType.FOOTBALL,
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(8),
                new BigDecimal("50000"), 1L, List.of(1L, 2L));
        TournamentResponse response = tournamentService.create(request);
        assertEquals(100L, response.getId());
        assertEquals("Осенний Кубок", response.getTitle());
        assertEquals("FOOTBALL", response.getSportType());
        assertEquals("Футбол", response.getSportDisplayName());
        assertEquals(2, response.getTeamsCount());
        assertEquals(1L, response.getRefereeId());
        assertTrue(response.getStandingsTable().contains("Зенит"));
    }
    @Test
    @DisplayName("create: тренер не может быть назначен судьёй турнира")
    void create_withNonRefereeUser_shouldThrowException() {
        CoachUser coach = new CoachUser(2L, "coach@test.com", "hash", "Сергей", "Тренеров");
        when(userRepository.findById(2L)).thenReturn(Optional.of(coach));
        TournamentRequest request = new TournamentRequest("Кубок", SportType.FOOTBALL,
                null, null, null, 2L, List.of());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> tournamentService.create(request));
        assertTrue(exception.getMessage().contains("не является судьёй"));
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }
    @Test
    @DisplayName("create: дата окончания раньше даты начала недопустима")
    void create_withInvalidDates_shouldThrowException() {
        TournamentRequest request = new TournamentRequest("Кубок", SportType.CHESS,
                LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 1), null, null, List.of());
        assertThrows(IllegalArgumentException.class, () -> tournamentService.create(request));
    }
    @Test
    @DisplayName("registerTeam: добавляет команду в турнир")
    void registerTeam_shouldAddTeam() {
        Tournament tournament = new Tournament(100L, "Кубок", SportType.FOOTBALL,
                LocalDate.now(), LocalDate.now().plusDays(3), new BigDecimal("1000"));
        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(firstTeam));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TournamentResponse response = tournamentService.registerTeam(100L, 1L);
        assertEquals(1, response.getTeamsCount());
        assertEquals(List.of(1L), response.getTeamIds());
    }
    @Test
    @DisplayName("registerTeam: повторная регистрация той же команды запрещена")
    void registerTeam_twice_shouldThrowException() {
        Tournament tournament = new Tournament(100L, "Кубок", SportType.FOOTBALL,
                LocalDate.now(), LocalDate.now().plusDays(3), new BigDecimal("1000"));
        tournament.addTeam(firstTeam);
        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(firstTeam));
        assertThrows(IllegalArgumentException.class, () -> tournamentService.registerTeam(100L, 1L));
    }
    @Test
    @DisplayName("unregisterTeam: исключает команду из турнира")
    void unregisterTeam_shouldRemoveTeam() {
        Tournament tournament = new Tournament(100L, "Кубок", SportType.FOOTBALL,
                LocalDate.now(), LocalDate.now().plusDays(3), new BigDecimal("1000"));
        tournament.addTeam(firstTeam);
        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TournamentResponse response = tournamentService.unregisterTeam(100L, 1L);
        assertEquals(0, response.getTeamsCount());
    }
    @Test
    @DisplayName("delete: турнир с матчами удалить нельзя")
    void delete_tournamentWithMatches_shouldThrowException() {
        Tournament tournament = new Tournament(100L, "Кубок", SportType.FOOTBALL,
                LocalDate.now(), LocalDate.now().plusDays(3), new BigDecimal("1000"));
        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(matchRepository.countByTournamentId(100L)).thenReturn(3L);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> tournamentService.delete(100L));
        assertTrue(exception.getMessage().contains("матчей — 3"));
        verify(tournamentRepository, never()).delete(any(Tournament.class));
    }
    @Test
    @DisplayName("getStandingsTable: возвращает таблицу с участниками")
    void getStandingsTable_shouldContainTeams() {
        Tournament tournament = new Tournament(100L, "Кубок", SportType.FOOTBALL,
                LocalDate.now(), LocalDate.now().plusDays(3), new BigDecimal("1000"));
        tournament.addTeam(firstTeam);
        tournament.addTeam(secondTeam);
        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        String table = tournamentService.getStandingsTable(100L);
        assertTrue(table.contains("ТУРНИРНАЯ ТАБЛИЦА"));
        assertTrue(table.contains("Зенит"));
        assertTrue(table.contains("Спартак"));
    }
    @Test
    @DisplayName("findByFilter: фильтр по виду спорта")
    void findByFilter_bySportType_shouldUseSportQuery() {
        Tournament tournament = new Tournament(100L, "Dota Major", SportType.E_SPORTS,
                LocalDate.now(), LocalDate.now().plusDays(3), BigDecimal.ZERO);
        when(tournamentRepository.findBySportType(SportType.E_SPORTS)).thenReturn(List.of(tournament));
        TournamentFilter filter = new TournamentFilter();
        filter.setSportType(SportType.E_SPORTS);
        List<TournamentResponse> tournaments = tournamentService.findByFilter(filter);
        assertEquals(1, tournaments.size());
        assertEquals("E_SPORTS", tournaments.get(0).getSportType());
        assertEquals("Киберспорт", tournaments.get(0).getSportDisplayName());
    }
    @Test
    @DisplayName("findById: отсутствующий турнир приводит к IllegalArgumentException")
    void findById_whenMissing_shouldThrowException() {
        when(tournamentRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> tournamentService.findById(404L));
    }
}
