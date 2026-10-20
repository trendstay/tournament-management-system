package com.tournament.service;

import com.tournament.model.enums.MatchStatus;
import com.tournament.model.match.Match;
import com.tournament.model.team.Team;
import com.tournament.repository.InMemoryMatchRepository;
import com.tournament.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-тесты сервиса матчей, включая многопоточную обработку коллекции.
 */
class MatchServiceTest {

    private MatchService matchService;
    private Team homeTeam;
    private Team awayTeam;

    @BeforeEach
    void setUp() {
        matchService = new MatchService(new InMemoryMatchRepository());
        homeTeam = TestFixtures.team("Зенит");
        awayTeam = TestFixtures.team("Спартак");
    }

    @Test
    void scheduleMatch_shouldAssignIdentifierAndSaveMatch() {
        Match match = new Match(homeTeam, awayTeam, LocalDateTime.now(), null, null);

        Match savedMatch = matchService.scheduleMatch(match);

        assertNotNull(savedMatch.getId());
        assertEquals(1, matchService.findAll().size());
    }

    @Test
    void finishMatch_shouldSetScoreAndStatus() {
        Match match = matchService.scheduleMatch(
                new Match(homeTeam, awayTeam, LocalDateTime.now(), null, null));

        Match finishedMatch = matchService.finishMatch(match.getId(), "3:1");

        assertEquals("3:1", finishedMatch.getScore());
        assertEquals(MatchStatus.FINISHED, finishedMatch.getStatus());
        assertEquals(1, matchService.findByStatus(MatchStatus.FINISHED).size());
    }

    @Test
    void finishMatch_whenMatchDoesNotExist_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> matchService.finishMatch(999L, "1:0"));

        assertTrue(exception.getMessage().contains("не найден"));
    }

    @Test
    void processMatchesConcurrently_shouldProcessWholeCollection() {
        List<Match> matches = createMatches(5);

        MatchProcessingReport report = matchService.processMatchesConcurrently(matches);

        assertEquals(5, report.getProcessedMatches());
        assertEquals(5, matchService.findByStatus(MatchStatus.FINISHED).size());
    }

    @Test
    void processMatchesConcurrently_shouldUseSeveralNamedThreads() {
        List<Match> matches = createMatches(5);

        MatchProcessingReport report = matchService.processMatchesConcurrently(matches);

        assertTrue(report.getWorkerThreadNames().size() >= 2,
                "Ожидалось не менее двух потоков, но были: " + report.getWorkerThreadNames());
        for (String threadName : report.getWorkerThreadNames()) {
            assertTrue(threadName.startsWith("match-worker-"),
                    "Неожиданное имя потока: " + threadName);
        }
        assertTrue(report.getDurationMillis() >= 0);
    }

    @Test
    void processMatchesConcurrently_whenCollectionIsEmpty_shouldReturnEmptyReport() {
        MatchProcessingReport report = matchService.processMatchesConcurrently(new ArrayList<>());

        assertEquals(0, report.getProcessedMatches());
        assertTrue(report.getWorkerThreadNames().isEmpty());
    }

    private List<Match> createMatches(int count) {
        List<Match> matches = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            Match match = new Match(null, homeTeam, awayTeam, LocalDateTime.now().plusDays(index));
            matchService.scheduleMatch(match);
            matches.add(match);
        }
        return matches;
    }
}
