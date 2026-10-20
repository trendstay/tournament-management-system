package com.tournament.model.match;

import com.tournament.model.enums.MatchStatus;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import com.tournament.model.user.Referee;
import com.tournament.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-тесты матча: счёт, победитель, протокол.
 */
class MatchTest {

    private Match match;
    private Team homeTeam;
    private Team awayTeam;

    @BeforeEach
    void setUp() {
        homeTeam = TestFixtures.team("Зенит", "Санкт-Петербург");
        awayTeam = TestFixtures.team("Спартак", "Москва");
        match = new Match(1L, homeTeam, awayTeam, LocalDateTime.now());
    }

    @Test
    void updateScore_withValidScore_shouldParseGoals() {
        match.updateScore("2:1");

        assertEquals("2:1", match.getScore());
        assertEquals(2, match.getHomeGoals());
        assertEquals(1, match.getAwayGoals());
    }

    @Test
    void updateScore_withInvalidScore_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> match.updateScore("invalid-score"));

        assertTrue(exception.getMessage().contains("Некорректный формат счёта"));
    }

    @Test
    void updateScore_whenNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> match.updateScore(null));
    }

    @Test
    void getWinner_whenHomeTeamWins_shouldReturnHomeTeam() {
        match.updateScore("3:0");
        match.setStatus(MatchStatus.FINISHED);

        assertEquals(homeTeam, match.getWinner());
    }

    @Test
    void getWinner_whenAwayTeamWins_shouldReturnAwayTeam() {
        match.updateScore("0:2");
        match.setStatus(MatchStatus.FINISHED);

        assertEquals(awayTeam, match.getWinner());
    }

    @Test
    void getWinner_whenDraw_shouldReturnNullAndMarkDraw() {
        match.updateScore("1:1");
        match.setStatus(MatchStatus.FINISHED);

        assertNull(match.getWinner());
        assertTrue(match.isDraw());
    }

    @Test
    void getWinner_whenMatchIsNotFinished_shouldReturnNull() {
        match.updateScore("3:0");

        assertNull(match.getWinner());
        assertFalse(match.isDraw());
    }

    @Test
    void generateMatchReport_shouldContainTeamsScoreAndDrawResult() {
        match.updateScore("1:1");
        match.setStatus(MatchStatus.FINISHED);

        String report = match.generateMatchReport();

        assertTrue(report.contains("Зенит vs Спартак"));
        assertTrue(report.contains("1:1"));
        assertTrue(report.contains("зафиксирована ничья"));
    }

    @Test
    void generateMatchReport_shouldContainTournamentAndReferee() {
        Tournament tournament = TestFixtures.tournament("Кубок", LocalDate.now().plusDays(5));
        Referee referee = TestFixtures.referee("referee@test.com");
        Match tournamentMatch = new Match(null, homeTeam, awayTeam,
                LocalDateTime.now(), tournament, referee);

        String report = tournamentMatch.generateMatchReport();

        assertTrue(report.contains("Кубок"));
        assertTrue(report.contains(referee.getFullName()));
        assertTrue(report.contains("Сергей Судейкин"));
    }

    @Test
    void constructor_whenTeamPlaysAgainstItself_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Match(null, homeTeam, TestFixtures.team("ЗЕНИТ"), LocalDateTime.now()));
    }

    @Test
    void constructor_whenMatchDateIsNull_shouldThrowException() {
        assertThrows(NullPointerException.class,
                () -> new Match(null, homeTeam, awayTeam, null));
    }
}
