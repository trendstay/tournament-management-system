package com.tournament.service;

import com.tournament.model.enums.SportType;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TournamentTest {
    private Tournament tournament;

    @BeforeEach
    void setUp() {
        tournament = new Tournament(1L, "Кубок", SportType.FOOTBALL,
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), new BigDecimal("10000"));
    }

    @Test
    void testIsRegistrationOpen_WhenWithinWindow_ShouldReturnTrue() {
        assertTrue(tournament.isRegistrationOpen());
    }

    @Test
    void testIsRegistrationOpen_WhenTournamentFarInFuture_ShouldReturnFalse() {
        tournament.setStartDate(LocalDate.now().plusDays(30));
        assertFalse(tournament.isRegistrationOpen());
    }

    @Test
    void testGenerateStandingsTable_ShouldFormatCorrectly() {
        tournament.addTeam(new Team(1L, "Динамо", "Москва"));
        String table = tournament.generateStandingsTable();

        assertNotNull(table);
        assertTrue(table.contains("ТУРНИРНАЯ ТАБЛИЦА"));
        assertTrue(table.contains("Динамо"));
        assertTrue(table.contains("Футбол"));
    }
}