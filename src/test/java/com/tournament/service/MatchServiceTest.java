package com.tournament.service;

import com.tournament.model.enums.MatchStatus;
import com.tournament.model.match.Match;
import com.tournament.model.team.Team;
import com.tournament.repository.InMemoryMatchRepository;
import com.tournament.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MatchServiceTest {
    private Match match;
    private Team homeTeam;
    private Team awayTeam;

    @BeforeEach
    void setUp() {
        homeTeam = new Team(1L, "Зенит", "СПб");
        awayTeam = new Team(2L, "Спартак", "Москва");
        match = new Match(1L, homeTeam, awayTeam, LocalDateTime.now());
    }

    @Test
    void testUpdateScore_ValidScore_ShouldParseGoalsCorrectly() {
        match.updateScore("2:1");
        assertEquals("2:1", match.getScore());
        assertEquals(2, match.getHomeGoals());
        assertEquals(1, match.getAwayGoals());
    }

    @Test
    void testUpdateScore_InvalidScore_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            match.updateScore("invalid-score");
        });
        assertTrue(exception.getMessage().contains("Некорректный формат счета"));
    }

    @Test
    void testGetWinner_WhenHomeTeamWins_ShouldReturnHomeTeam() {
        match.updateScore("3:0");
        match.setStatus(MatchStatus.FINISHED);

        assertEquals(homeTeam, match.getWinner());
    }

    @Test
    void testGenerateMatchReport_ShouldContainKeyInformation() {
        match.updateScore("1:1");
        match.setStatus(MatchStatus.FINISHED);

        String report = match.generateMatchReport();

        assertNotNull(report);
        assertTrue(report.contains("Зенит vs Спартак"));
        assertTrue(report.contains("1:1"));
        assertTrue(report.contains("Зафиксирована ничья"));
    }

    @Test
    void testFinishMatch_AndProcessMatchesConcurrently() {
        MatchRepository repository = new InMemoryMatchRepository();
        MatchService service = new MatchService(repository);

        Match savedMatch = repository.save(new Match(null, homeTeam, awayTeam, LocalDateTime.now()));
        service.finishMatch(savedMatch.getId(), "2:0");

        assertEquals(MatchStatus.FINISHED, repository.findById(savedMatch.getId()).orElseThrow().getStatus());

        Match secondMatch = repository.save(new Match(null, homeTeam, awayTeam, LocalDateTime.now()));
        service.processMatchesConcurrently(repository.findAll());

        assertEquals(MatchStatus.FINISHED, repository.findById(secondMatch.getId()).orElseThrow().getStatus());
        assertTrue(repository.delete(secondMatch.getId()));
    }
}