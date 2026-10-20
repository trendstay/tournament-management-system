package com.tournament;

import com.tournament.model.enums.SportType;
import com.tournament.model.match.Match;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import com.tournament.model.user.Admin;
import com.tournament.model.user.Captain;
import com.tournament.model.user.Coach;
import com.tournament.model.user.Manager;
import com.tournament.model.user.Player;
import com.tournament.model.user.Referee;
import com.tournament.model.user.User;
import com.tournament.repository.InMemoryMatchRepository;
import com.tournament.repository.InMemoryTeamRepository;
import com.tournament.repository.InMemoryTournamentRepository;
import com.tournament.repository.InMemoryUserRepository;
import com.tournament.repository.MatchRepository;
import com.tournament.repository.TeamRepository;
import com.tournament.repository.TournamentRepository;
import com.tournament.repository.UserRepository;
import com.tournament.service.MatchProcessingReport;
import com.tournament.service.MatchService;
import com.tournament.service.TeamService;
import com.tournament.service.TournamentService;
import com.tournament.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Точка входа приложения и демонстрация работы сервиса.
 * <p>
 * Показывает регистрацию пользователей разных ролей, формирование команд,
 * проведение турнира и многопоточную обработку коллекции матчей.
 */
@SpringBootApplication
public class TournamentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TournamentServiceApplication.class, args);

        UserRepository userRepository = new InMemoryUserRepository();
        TeamRepository teamRepository = new InMemoryTeamRepository();
        TournamentRepository tournamentRepository = new InMemoryTournamentRepository();
        MatchRepository matchRepository = new InMemoryMatchRepository();

        UserService userService = new UserService(userRepository);
        TeamService teamService = new TeamService(teamRepository);
        TournamentService tournamentService = new TournamentService(tournamentRepository);
        MatchService matchService = new MatchService(matchRepository);

        Admin admin = userService.registerAdmin(
                "admin@tournament.com", "pass123", "Иван", "Администратор", "+79990000001");
        Manager manager = userService.registerManager(
                "manager@tournament.com", "pass123", "Пётр", "Менеджеров", "+79990000002");
        Referee referee = userService.registerReferee(
                "referee@tournament.com", "pass123", "Сергей", "Судейкин", "+79990000003");
        Coach coach = userService.registerCoach(
                "coach@tournament.com", "pass123", "Олег", "Тренеров", "+79990000004");
        Captain captain = userService.registerCaptain(
                "captain@tournament.com", "pass123", "Артём", "Капитанов", "+79990000005");
        Player player = userService.registerPlayer(
                "player@tournament.com", "pass123", "Алексей", "Игроков", "+79990000006");

        System.out.println("\n=== СЕРВИС ОРГАНИЗАЦИИ СПОРТИВНЫХ ТУРНИРОВ ===\n");
        System.out.println("Зарегистрировано пользователей: " + userService.count());
        for (User registeredUser : userService.findAll()) {
            System.out.printf("  %-12s %s (%s)%n",
                    registeredUser.getRole().getDisplayName(),
                    registeredUser.getFullName(),
                    registeredUser.getEmail());
        }
        System.out.println("Администратор может управлять пользователями: " + admin.canManageUsers());
        System.out.println("Менеджер может создавать турниры: " + manager.canCreateTournament());
        System.out.println("Судья может судить матчи: " + referee.canRefereeMatches());

        Team teamA = teamService.createTeam("Зенит", "Санкт-Петербург");
        teamService.assignCoach(teamA.getId(), coach);
        teamService.appointCaptain(teamA.getId(), captain);
        teamService.addPlayer(teamA.getId(), player);

        Coach secondCoach = userService.registerCoach(
                "coach2@tournament.com", "pass123", "Дмитрий", "Наставников", "+79990000007");
        Captain secondCaptain = userService.registerCaptain(
                "captain2@tournament.com", "pass123", "Никита", "Лидеров", "+79990000008");
        Player secondPlayer = userService.registerPlayer(
                "player2@tournament.com", "pass123", "Максим", "Быстров", "+79990000009");

        Team teamB = teamService.createTeam("Спартак", "Москва");
        teamService.assignCoach(teamB.getId(), secondCoach);
        teamService.appointCaptain(teamB.getId(), secondCaptain);
        teamService.addPlayer(teamB.getId(), secondPlayer);

        Tournament tournament = tournamentService.createTournament(
                "Осенний Кубок",
                SportType.FOOTBALL,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                new BigDecimal("50000"));
        tournamentService.assignReferee(tournament.getId(), referee);
        tournamentService.registerTeam(tournament.getId(), teamA);
        tournamentService.registerTeam(tournament.getId(), teamB);

        System.out.println("\nТурнир «" + tournament.getTitle() + "», судья: "
                + tournament.getReferee().getFullName());
        System.out.println("Регистрация открыта: " + tournament.isRegistrationOpen());
        System.out.println("\n" + tournament.generateStandingsTable());

        Match centralMatch = new Match(
                teamA, teamB, LocalDateTime.now().plusDays(6), tournament, referee);
        matchService.scheduleMatch(centralMatch);
        matchService.finishMatch(centralMatch.getId(), "3:1");
        System.out.println(centralMatch.generateMatchReport());

        System.out.println("### МНОГОПОТОЧНАЯ ОБРАБОТКА МАТЧЕЙ ###");
        List<Match> matchesForProcessing = new ArrayList<>();
        for (int index = 0; index < 5; index++) {
            Match match = new Match(
                    index % 2 == 0 ? teamA : teamB,
                    index % 2 == 0 ? teamB : teamA,
                    LocalDateTime.now().plusDays(7 + index),
                    tournament,
                    referee);
            matchService.scheduleMatch(match);
            matchesForProcessing.add(match);
        }

        MatchProcessingReport report = matchService.processMatchesConcurrently(matchesForProcessing);
        System.out.println(report.getSummary());
    }
}
