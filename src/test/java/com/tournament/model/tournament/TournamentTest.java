package com.tournament.model.tournament;

import com.tournament.model.enums.SportType;
import com.tournament.model.team.Team;
import com.tournament.model.user.Referee;
import com.tournament.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-тесты турнира: регистрация команд, судья, турнирная таблица.
 */
class TournamentTest {

    private Tournament tournament;

    @BeforeEach
    void setUp() {
        tournament = new Tournament(1L, "Кубок", SportType.FOOTBALL,
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(10),
                new BigDecimal("10000"));
    }

    @Test
    void isRegistrationOpen_whenStartWithinTwoWeeks_shouldReturnTrue() {
        assertTrue(tournament.isRegistrationOpen());
    }

    @Test
    void isRegistrationOpen_whenStartIsFarInFuture_shouldReturnFalse() {
        tournament.setEndDate(LocalDate.now().plusDays(35));
        tournament.setStartDate(LocalDate.now().plusDays(30));

        assertFalse(tournament.isRegistrationOpen());
    }

    @Test
    void isRegistrationOpen_whenTournamentAlreadyStarted_shouldReturnFalse() {
        tournament.setStartDate(LocalDate.now().minusDays(1));
        tournament.setEndDate(LocalDate.now().plusDays(3));

        assertFalse(tournament.isRegistrationOpen());
    }

    @Test
    void addTeam_shouldRegisterTeam() {
        Team team = TestFixtures.team("Динамо");

        tournament.addTeam(team);

        assertEquals(1, tournament.getTeamsCount());
        assertTrue(tournament.findTeamByName("динамо").isPresent());
    }

    @Test
    void addTeam_whenNull_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> tournament.addTeam(null));
    }

    @Test
    void addTeam_whenTeamAlreadyRegistered_shouldThrowException() {
        tournament.addTeam(TestFixtures.team("Динамо"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> tournament.addTeam(TestFixtures.team("динамо")));

        assertTrue(exception.getMessage().contains("уже зарегистрирована"));
        assertEquals(1, tournament.getTeamsCount());
    }

    @Test
    void removeTeam_shouldUnregisterTeam() {
        Team team = TestFixtures.team("Динамо");
        tournament.addTeam(team);

        assertTrue(tournament.removeTeam(team));
        assertEquals(0, tournament.getTeamsCount());
    }

    @Test
    void setReferee_shouldAssignMainReferee() {
        Referee referee = TestFixtures.referee("referee@test.com");

        tournament.setReferee(referee);

        assertTrue(tournament.hasReferee());
        assertEquals(referee, tournament.getReferee());
    }

    @Test
    void generateStandingsTable_shouldContainTitleSportAndTeams() {
        Team team = TestFixtures.team("Динамо");
        team.addPlayer(TestFixtures.player("player@test.com"));
        tournament.addTeam(team);

        String table = tournament.generateStandingsTable();

        assertTrue(table.contains("ТУРНИРНАЯ ТАБЛИЦА"));
        assertTrue(table.contains("Кубок"));
        assertTrue(table.contains("Футбол"));
        assertTrue(table.contains("Динамо"));
        assertTrue(table.contains("Игроки"));
    }

    @Test
    void constructor_whenEndDateBeforeStartDate_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Tournament(
                null, "Некорректный", SportType.FOOTBALL,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(5),
                BigDecimal.TEN));
    }

    @Test
    void setPrizePool_whenNegative_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> tournament.setPrizePool(new BigDecimal("-1")));
    }

    @Test
    void setPrizePool_whenNull_shouldUseZero() {
        tournament.setPrizePool(null);

        assertEquals(BigDecimal.ZERO, tournament.getPrizePool());
    }
}
