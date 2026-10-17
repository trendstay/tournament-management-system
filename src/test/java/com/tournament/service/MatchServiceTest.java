package com.tournament.model.match;

import com.tournament.model.enums.MatchStatus;
import com.tournament.model.team.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class MatchTest {
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
}