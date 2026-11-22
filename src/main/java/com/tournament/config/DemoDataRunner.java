package com.tournament.config;
import com.tournament.dto.match.FinishMatchRequest;
import com.tournament.dto.match.MatchResponse;
import com.tournament.dto.match.MatchRequest;
import com.tournament.dto.team.TeamRequest;
import com.tournament.dto.tournament.TournamentRequest;
import com.tournament.dto.tournament.TournamentResponse;
import com.tournament.dto.team.TeamResponse;
import com.tournament.dto.user.UserRequest;
import com.tournament.dto.user.UserResponse;
import com.tournament.model.enums.SportType;
import com.tournament.model.enums.UserRole;
import com.tournament.service.MatchService;
import com.tournament.service.TeamService;
import com.tournament.service.TournamentService;
import com.tournament.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
/**
 * Демонстрационное заполнение базы данных через сервисный слой
 * (работа с данными «из кода», пункт задания про CRUD-операции).
 *
 * <p>Активируется только профилем {@code demo}:
 * {@code mvn spring-boot:run -Dspring-boot.run.profiles=demo}.</p>
 */
@Component
@Profile("demo")
public class DemoDataRunner implements ApplicationRunner {
    private final UserService userService;
    private final TeamService teamService;
    private final TournamentService tournamentService;
    private final MatchService matchService;
    public DemoDataRunner(UserService userService,
                          TeamService teamService,
                          TournamentService tournamentService,
                          MatchService matchService) {
        this.userService = userService;
        this.teamService = teamService;
        this.tournamentService = tournamentService;
        this.matchService = matchService;
    }
    @Override
    public void run(ApplicationArguments args) {
        if (!userService.findAll().isEmpty()) {
            System.out.println("Демо-данные уже загружены, инициализация пропущена");
            return;
        }
        UserResponse admin = userService.create(new UserRequest("admin@tournament.com", "pass123",
                "Иван", "Администратор", UserRole.ADMIN, null, null));
        userService.create(new UserRequest("manager@tournament.com", "pass123",
                "Пётр", "Менеджер", UserRole.MANAGER, null, null));
        UserResponse referee = userService.create(new UserRequest("referee@tournament.com", "pass123",
                "Николай", "Судьин", UserRole.REFEREE, null, null));
        UserResponse coach = userService.create(new UserRequest("coach@tournament.com", "pass123",
                "Сергей", "Тренеров", UserRole.COACH, null, null));
        TeamResponse firstTeam = teamService.create(new TeamRequest("Зенит", "Санкт-Петербург", coach.getId()));
        TeamResponse secondTeam = teamService.create(new TeamRequest("Спартак", "Москва", null));
        UserResponse player = userService.create(new UserRequest("player@tournament.com", "pass123",
                "Алексей", "Игроков", UserRole.PLAYER, "+79991234567", firstTeam.getId()));
        teamService.appointCaptain(firstTeam.getId(), player.getId());
        TournamentResponse tournament = tournamentService.create(new TournamentRequest(
                "Осенний Кубок",
                SportType.FOOTBALL,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10),
                new BigDecimal("50000"),
                referee.getId(),
                List.of(firstTeam.getId(), secondTeam.getId())
        ));
        MatchResponse match = matchService.create(new MatchRequest(firstTeam.getId(), secondTeam.getId(),
                tournament.getId(), LocalDateTime.now(), "0:0"));
        MatchResponse finishedMatch = matchService.finishMatch(match.getId(), new FinishMatchRequest("3:1"));
        System.out.println("=== ДЕМО-ДАННЫЕ ЗАГРУЖЕНЫ (администратор: " + admin.getEmail() + ") ===");
        System.out.println(tournamentService.getStandingsTable(tournament.getId()));
        System.out.println(finishedMatch.getMatchReport());
    }
}
