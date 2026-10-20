package com.tournament.model.team;

import com.tournament.model.user.Captain;
import com.tournament.model.user.Coach;
import com.tournament.model.user.Player;
import com.tournament.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-тесты команды: состав, тренер, капитан.
 */
class TeamTest {

    private Team team;
    private Player firstPlayer;
    private Player secondPlayer;

    @BeforeEach
    void setUp() {
        team = TestFixtures.team("Динамо");
        firstPlayer = TestFixtures.player("first@test.com");
        secondPlayer = TestFixtures.player("second@test.com");
    }

    @Test
    void addPlayer_shouldAddPlayerToRoster() {
        team.addPlayer(firstPlayer);

        assertEquals(1, team.getPlayersCount());
        assertEquals(firstPlayer, team.getPlayers().get(0));
    }

    @Test
    void addPlayer_whenNull_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> team.addPlayer(null));
    }

    @Test
    void addPlayer_whenEmailAlreadyExists_shouldThrowException() {
        team.addPlayer(firstPlayer);
        Player duplicate = TestFixtures.player("FIRST@test.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> team.addPlayer(duplicate));

        assertTrue(exception.getMessage().contains("уже состоит в команде"));
        assertEquals(1, team.getPlayersCount());
    }

    @Test
    void setCaptain_shouldAutomaticallyAddCaptainToRoster() {
        Captain captain = TestFixtures.captain("captain@test.com");

        team.setCaptain(captain);

        assertEquals(captain, team.getCaptain());
        assertEquals(1, team.getPlayersCount());
        assertTrue(team.findPlayerByEmail("captain@test.com").isPresent());
    }

    @Test
    void setCoach_shouldStoreCoach() {
        Coach coach = TestFixtures.coach("coach@test.com");

        team.setCoach(coach);

        assertEquals(coach, team.getCoach());
    }

    @Test
    void removePlayerByEmail_shouldRemovePlayer() {
        team.addPlayer(firstPlayer);
        team.addPlayer(secondPlayer);

        assertTrue(team.removePlayerByEmail("FIRST@test.com"));
        assertEquals(1, team.getPlayersCount());
        assertFalse(team.findPlayerByEmail("first@test.com").isPresent());
    }

    @Test
    void findPlayerByFullName_shouldReturnMatchingPlayer() {
        team.addPlayer(firstPlayer);

        assertTrue(team.findPlayerByFullName("Алексей", "Игроков").isPresent());
        assertFalse(team.findPlayerByFullName("Иван", "Иванов").isPresent());
    }

    @Test
    void getPlayers_shouldReturnUnmodifiableView() {
        team.addPlayer(firstPlayer);

        assertThrows(UnsupportedOperationException.class,
                () -> team.getPlayers().add(secondPlayer));
    }

    @Test
    void isReady_shouldBeTrueOnlyWhenCoachCaptainAndPlayersExist() {
        assertFalse(team.isReady());

        team.setCoach(TestFixtures.coach("coach@test.com"));
        team.setCaptain(TestFixtures.captain("captain@test.com"));

        assertTrue(team.isReady());
    }

    @Test
    void teamsWithSameName_shouldBeEqual() {
        Team anotherTeam = TestFixtures.team("ДИНАМО", "Санкт-Петербург");

        assertEquals(team, anotherTeam);
        assertEquals(team.hashCode(), anotherTeam.hashCode());
    }
}
