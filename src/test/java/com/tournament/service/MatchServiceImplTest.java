package com.tournament.service;
import com.tournament.dto.match.BatchMatchRequest;
import com.tournament.dto.match.FinishMatchRequest;
import com.tournament.dto.match.MatchFilter;
import com.tournament.dto.match.MatchRequest;
import com.tournament.dto.match.MatchResponse;
import com.tournament.model.enums.MatchStatus;
import com.tournament.model.enums.SportType;
import com.tournament.model.match.Match;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import com.tournament.support.AbstractRepositoryMockSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * Тесты сервиса матчей. База данных не используется — репозитории замоканы.
 */
@DisplayName("MatchService: CRUD, завершение матчей и фильтры")
class MatchServiceImplTest extends AbstractRepositoryMockSupport {
    @Autowired
    private MatchService matchService;
    private Team homeTeam;
    private Team awayTeam;
    @BeforeEach
    void setUp() {
        homeTeam = new Team(1L, "Зенит", "Санкт-Петербург");
        awayTeam = new Team(2L, "Спартак", "Москва");
    }
    @Test
    @DisplayName("create: создаёт запланированный матч между двумя командами")
    void create_shouldCreateScheduledMatch() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(awayTeam));
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> {
            Match match = invocation.getArgument(0);
            match.setId(50L);
            return match;
        });
        MatchResponse response = matchService.create(new MatchRequest(1L, 2L, null, LocalDateTime.now(), null));
        assertEquals(50L, response.getId());
        assertEquals("0:0", response.getScore());
        assertEquals("SCHEDULED", response.getStatus());
        assertEquals("Зенит", response.getHomeTeamName());
    }
    @Test
    @DisplayName("create: команда не может играть сама с собой")
    void create_withSameTeams_shouldThrowException() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        assertThrows(IllegalArgumentException.class,
                () -> matchService.create(new MatchRequest(1L, 1L, null, LocalDateTime.now(), null)));
        verify(matchRepository, never()).save(any(Match.class));
    }
    @Test
    @DisplayName("finishMatch: выставляет счёт и статус FINISHED, определяет победителя")
    void finishMatch_shouldSetScoreAndStatus() {
        Match match = new Match(50L, homeTeam, awayTeam, LocalDateTime.now());
        when(matchRepository.findById(50L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));
        MatchResponse response = matchService.finishMatch(50L, new FinishMatchRequest("3:1"));
        assertEquals("3:1", response.getScore());
        assertEquals("FINISHED", response.getStatus());
        assertEquals("Зенит", response.getWinnerTeamName());
        assertTrue(response.getMatchReport().contains("Победитель — Зенит"));
    }
    @Test
    @DisplayName("finishMatch: некорректный формат счёта приводит к IllegalArgumentException")
    void finishMatch_withInvalidScore_shouldThrowException() {
        Match match = new Match(50L, homeTeam, awayTeam, LocalDateTime.now());
        when(matchRepository.findById(50L)).thenReturn(Optional.of(match));
        assertThrows(IllegalArgumentException.class,
                () -> matchService.finishMatch(50L, new FinishMatchRequest("три-один")));
    }
    @Test
    @DisplayName("processMatchesConcurrently: пакетно завершает матчи в нескольких потоках")
    void processMatchesConcurrently_shouldFinishAllMatches() {
        Match firstMatch = new Match(1L, homeTeam, awayTeam, LocalDateTime.now());
        Match secondMatch = new Match(2L, awayTeam, homeTeam, LocalDateTime.now());
        Match thirdMatch = new Match(3L, homeTeam, awayTeam, LocalDateTime.now());
        when(matchRepository.findById(1L)).thenReturn(Optional.of(firstMatch));
        when(matchRepository.findById(2L)).thenReturn(Optional.of(secondMatch));
        when(matchRepository.findById(3L)).thenReturn(Optional.of(thirdMatch));
        when(matchRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        List<MatchResponse> responses = matchService.processMatchesConcurrently(
                new BatchMatchRequest(List.of(1L, 2L, 3L)));
        assertEquals(3, responses.size());
        for (MatchResponse response : responses) {
            assertEquals("FINISHED", response.getStatus());
        }
    }
    @Test
    @DisplayName("findByFilter: фильтр по статусу матча")
    void findByFilter_byStatus_shouldUseStatusQuery() {
        Match finishedMatch = new Match(1L, homeTeam, awayTeam, LocalDateTime.now());
        finishedMatch.setStatus(MatchStatus.FINISHED);
        when(matchRepository.findByStatus(MatchStatus.FINISHED)).thenReturn(List.of(finishedMatch));
        MatchFilter filter = new MatchFilter();
        filter.setStatus("finished");
        List<MatchResponse> matches = matchService.findByFilter(filter);
        assertEquals(1, matches.size());
        assertEquals("FINISHED", matches.get(0).getStatus());
    }
    @Test
    @DisplayName("findByFilter: неизвестный статус матча отклоняется")
    void findByFilter_withUnknownStatus_shouldThrowException() {
        MatchFilter filter = new MatchFilter();
        filter.setStatus("UNKNOWN");
        assertThrows(IllegalArgumentException.class, () -> matchService.findByFilter(filter));
    }
    @Test
    @DisplayName("findByFilter: фильтр по турниру")
    void findByFilter_byTournament_shouldUseTournamentQuery() {
        Tournament tournament = new Tournament(7L, "Кубок", SportType.FOOTBALL,
                LocalDate.now(), LocalDate.now().plusDays(5), new BigDecimal("100"));
        Match match = new Match(1L, homeTeam, awayTeam, LocalDateTime.now());
        match.setTournament(tournament);
        when(matchRepository.findByTournamentId(7L)).thenReturn(List.of(match));
        MatchFilter filter = new MatchFilter();
        filter.setTournamentId(7L);
        List<MatchResponse> matches = matchService.findByFilter(filter);
        assertEquals(1, matches.size());
        assertEquals("Кубок", matches.get(0).getTournamentTitle());
    }
    @Test
    @DisplayName("create: матч привязывается к турниру")
    void create_withTournament_shouldLinkMatch() {
        Tournament tournament = new Tournament(7L, "Кубок", SportType.FOOTBALL,
                LocalDate.now(), LocalDate.now().plusDays(5), new BigDecimal("100"));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(awayTeam));
        when(tournamentRepository.findById(7L)).thenReturn(Optional.of(tournament));
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));
        MatchResponse response = matchService.create(
                new MatchRequest(1L, 2L, 7L, LocalDateTime.now(), "2:2"));
        assertEquals(7L, response.getTournamentId());
        assertEquals("2:2", response.getScore());
        assertNotNull(response.getMatchReport());
    }
}
