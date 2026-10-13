package com.tournament;

import com.tournament.model.enums.SportType;
import com.tournament.model.match.Match;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import com.tournament.model.user.AdminUser;
import com.tournament.model.user.CustomerUser;
import com.tournament.model.user.ManagerUser;
import com.tournament.repository.InMemoryMatchRepository;
import com.tournament.repository.InMemoryUserRepository;
import com.tournament.repository.MatchRepository;
import com.tournament.repository.UserRepository;
import com.tournament.service.MatchService;
import com.tournament.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class TournamentServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(TournamentServiceApplication.class, args);

        System.out.println("\n=== СЕРВИС ОРГАНИЗАЦИИ СПОРТИВНЫХ ТУРНИРОВ ===\n");

        UserRepository userRepository = new InMemoryUserRepository();
        MatchRepository matchRepository = new InMemoryMatchRepository();

        UserService userService = new UserService(userRepository);
        MatchService matchService = new MatchService(matchRepository);

        // 1. Создание пользователей
        AdminUser admin = userService.registerAdmin("admin@tournament.com", "pass123", "Иван", "Администратор");
        ManagerUser manager = userService.registerManager("manager@tournament.com", "pass123", "Петр", "Менеджер");
        CustomerUser player1 = userService.registerCustomer("player1@tournament.com", "pass123", "Алексей", "Игроков", "+79991234567");

        // 2. Демонстрация турниров по РАЗНЫМ видам спорта
        Tournament footballTournament = new Tournament(1L, "Осенний Кубок", SportType.FOOTBALL,
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), new BigDecimal("50000"));

        Tournament esportsTournament = new Tournament(2L, "Dota 2 Major", SportType.E_SPORTS,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(7), new BigDecimal("500000"));

        System.out.println("Открыта регистрация на футбол? " + (footballTournament.isRegistrationOpen() ? "Да 🎉" : "Нет"));
        System.out.println("Вид спорта турнира #2: " + esportsTournament.getSportType().getDisplayName());

        Team teamA = new Team(1L, "Зенит", "Санкт-Петербург");
        Team teamB = new Team(2L, "Спартак", "Москва");
        footballTournament.addTeam(teamA);
        footballTournament.addTeam(teamB);

        // 3. Вывод турнирной таблицы
        System.out.println("\n" + footballTournament.generateStandingsTable());

        // 4. Создание и проведение матча
        Match match = new Match(1L, teamA, teamB, LocalDateTime.now());
        matchRepository.save(match);

        // Завершаем матч и вносим счет
        matchService.finishMatch(match.getId(), "3:1");

        // 5. Вывод сгенерированного протокола матча
        System.out.println(match.generateMatchReport());

        // 6. Многопоточная обработка
        System.out.println("\n### МНОГОПОТОЧНАЯ ОБРАБОТКА МАТЧЕЙ ###");
        List<Match> matches = matchRepository.findAll();
        matchService.processMatchesConcurrently(matches);
    }
}