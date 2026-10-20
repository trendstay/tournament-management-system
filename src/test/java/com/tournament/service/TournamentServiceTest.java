package com.tournament.service;

import com.tournament.model.enums.SportType;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import com.tournament.model.user.Referee;
import com.tournament.repository.InMemoryTournamentRepository;
import com.tournament.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-тесты сервиса турниров.
 */
class TournamentServiceTest {

    private TournamentService tournamentService;
    private LocalDate startDate;

    @BeforeEach
    void setUp() {
        tournamentService = new TournamentService(new InMemoryTournamentRepository());
        startDate = LocalDate.now().plusDays(5);
    }

    @Test
    void createTournament_shouldSaveTournamentWithIdentifier() {
        Tournament tournament = createFootballTournament("Осенний Кубок");

        assertNotNull(tournament.getId());
        assertEquals(SportType.FOOTBALL, tournament.getSportType());
        assertEquals(1, tournamentService.findAll().size());
    }

    @Test
    void createTournament_whenTitleAndStartDateAlreadyExist_shouldThrowException() {
        createFootballTournament("Осенний Кубок");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> createFootballTournament("осенний кубок"));

        assertTrue(exception.getMessage().contains("уже существует"));
    }

    @Test
    void createTournament_withSameTitleButAnotherDate_shouldBeAllowed() {
        createFootballTournament("Осенний Кубок");

        Tournament secondTournament = tournamentService.createTournament(
                "Осенний Кубок", SportType.FOOTBALL, startDate.plusDays(1),
                startDate.plusDays(11), BigDecimal.TEN);

        assertNotNull(secondTournament.getId());
        assertEquals(2, tournamentService.findAll().size());
    }

    @Test
    void assignReferee_shouldSetMainReferee() {
        Tournament tournament = createFootballTournament("Осенний Кубок");
        Referee referee = TestFixtures.referee("referee@test.com");

        tournamentService.assignReferee(tournament.getId(), referee);

        assertEquals(referee, tournamentService.findById(tournament.getId()).getReferee());
    }

    @Test
    void registerTeam_shouldAddTeamToTournament() {
        Tournament tournament = createFootballTournament("Осенний Кубок");
        Team team = TestFixtures.team("Динамо");

        tournamentService.registerTeam(tournament.getId(), team);

        assertEquals(1, tournamentService.findById(tournament.getId()).getTeamsCount());
    }

    @Test
    void registerTeam_whenTeamAlreadyRegistered_shouldThrowException() {
        Tournament tournament = createFootballTournament("Осенний Кубок");
        Team team = TestFixtures.team("Динамо");
        tournamentService.registerTeam(tournament.getId(), team);

        assertThrows(IllegalArgumentException.class,
                () -> tournamentService.registerTeam(tournament.getId(), TestFixtures.team("ДИНАМО")));
    }

    @Test
    void findBySportType_shouldReturnOnlyMatchingTournaments() {
        createFootballTournament("Футбольный Кубок");
        tournamentService.createTournament("Шахматный Кубок", SportType.CHESS,
                startDate, startDate.plusDays(3), BigDecimal.TEN);

        List<Tournament> footballTournaments = tournamentService.findBySportType(SportType.FOOTBALL);

        assertEquals(1, footballTournaments.size());
        assertEquals(SportType.FOOTBALL, footballTournaments.get(0).getSportType());
    }

    @Test
    void buildStandings_shouldContainTournamentTitle() {
        Tournament tournament = createFootballTournament("Осенний Кубок");
        tournamentService.registerTeam(tournament.getId(), TestFixtures.team("Динамо"));

        String standings = tournamentService.buildStandings(tournament.getId());

        assertTrue(standings.contains("Осенний Кубок"));
        assertTrue(standings.contains("Динамо"));
    }

    @Test
    void findById_whenTournamentDoesNotExist_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> tournamentService.findById(999L));

        assertTrue(exception.getMessage().contains("не найден"));
    }

    @Test
    void deleteTournament_shouldRemoveTournament() {
        Tournament tournament = createFootballTournament("Осенний Кубок");

        assertTrue(tournamentService.deleteTournament(tournament.getId()));
        assertTrue(tournamentService.findAll().isEmpty());
    }

    private Tournament createFootballTournament(String title) {
        return tournamentService.createTournament(
                title, SportType.FOOTBALL, startDate, startDate.plusDays(10), new BigDecimal("10000"));
    }
}
