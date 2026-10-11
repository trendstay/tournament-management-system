package com.tournament.service;

import com.tournament.model.enums.MatchStatus;
import com.tournament.model.match.Match;
import com.tournament.repository.InMemoryMatchRepository;
import com.tournament.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchServiceTest {

    private MatchRepository matchRepository;
    private MatchService matchService;

    @BeforeEach
    void setUp() {
        matchRepository = new InMemoryMatchRepository();
        matchService = new MatchService(matchRepository);
    }

    @Test
    void testProcessMatchesConcurrently_ShouldFinishAllMatches() {
        List<Match> matchList = new ArrayList<>();

        for (long i = 1; i <= 5; i++) {
            Match match = new Match();
            match.setId(i);
            match.setStatus(MatchStatus.SCHEDULED);
            matchRepository.save(match);
            matchList.add(match);
        }

        // Запуск многопоточной обработки
        matchService.processMatchesConcurrently(matchList);

        // Проверяем, что все матчи из переданной коллекции перешли в статус FINISHED
        List<Match> results = matchRepository.findAll();
        assertEquals(5, results.size());
        for (Match m : results) {
            assertEquals(MatchStatus.FINISHED, m.getStatus());
        }
    }
}