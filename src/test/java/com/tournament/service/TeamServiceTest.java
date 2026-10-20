package com.tournament.service;

import com.tournament.model.team.Team;
import com.tournament.model.user.Captain;
import com.tournament.model.user.Coach;
import com.tournament.model.user.Player;
import com.tournament.repository.InMemoryTeamRepository;
import com.tournament.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-тесты сервиса команд.
 */
class TeamServiceTest {

    private TeamService teamService;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(new InMemoryTeamRepository());
    }

    @Test
    void createTeam_shouldSaveTeamWithIdentifier() {
        Team team = teamService.createTeam("Динамо", "Москва");

        assertNotNull(team.getId());
        assertEquals("Динамо", team.getName());
        assertTrue(teamService.findByName("динамо").isPresent());
    }

    @Test
    void createTeam_whenNameAlreadyExists_shouldThrowException() {
        teamService.createTeam("Динамо", "Москва");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> teamService.createTeam("ДИНАМО", "Санкт-Петербург"));

        assertTrue(exception.getMessage().contains("уже существует"));
    }

    @Test
    void createTeam_whenNameIsBlank_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> teamService.createTeam("   ", "Москва"));
    }

    @Test
    void assignCoach_shouldStoreCoachInTeam() {
        Team team = teamService.createTeam("Динамо", "Москва");
        Coach coach = TestFixtures.coach("coach@test.com");

        teamService.assignCoach(team.getId(), coach);

        assertEquals(coach, teamService.findById(team.getId()).getCoach());
    }

    @Test
    void appointCaptain_shouldAddCaptainToRoster() {
        Team team = teamService.createTeam("Динамо", "Москва");
        Captain captain = TestFixtures.captain("captain@test.com");

        teamService.appointCaptain(team.getId(), captain);

        Team updatedTeam = teamService.findById(team.getId());
        assertEquals(captain, updatedTeam.getCaptain());
        assertEquals(1, updatedTeam.getPlayersCount());
    }

    @Test
    void addPlayer_shouldExtendRoster() {
        Team team = teamService.createTeam("Динамо", "Москва");
        Player player = TestFixtures.player("player@test.com");

        teamService.addPlayer(team.getId(), player);

        assertEquals(1, teamService.findById(team.getId()).getPlayersCount());
    }

    @Test
    void removePlayer_shouldRemovePlayerByEmail() {
        Team team = teamService.createTeam("Динамо", "Москва");
        Player player = TestFixtures.player("player@test.com");
        teamService.addPlayer(team.getId(), player);

        teamService.removePlayer(team.getId(), "PLAYER@test.com");

        assertEquals(0, teamService.findById(team.getId()).getPlayersCount());
    }

    @Test
    void findById_whenTeamDoesNotExist_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> teamService.findById(999L));

        assertTrue(exception.getMessage().contains("не найдена"));
    }

    @Test
    void deleteTeam_shouldRemoveTeamFromRepository() {
        Team team = teamService.createTeam("Динамо", "Москва");

        assertTrue(teamService.deleteTeam(team.getId()));
        assertTrue(teamService.findAll().isEmpty());
    }
}
