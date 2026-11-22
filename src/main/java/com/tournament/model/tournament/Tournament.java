package com.tournament.model.tournament;
import com.tournament.exception.ValidationException;
import com.tournament.model.enums.MatchStatus;
import com.tournament.model.enums.SportType;
import com.tournament.model.match.Match;
import com.tournament.model.team.Team;
import com.tournament.model.user.RefereeUser;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Класс спортивного турнира.
 * Хранится в таблице {@code tournaments}, команды связаны через
 * промежуточную таблицу {@code tournament_teams}.
 */
@Entity
@Table(name = "tournaments")
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                 // Первичный ключ
    @Column(nullable = false)
    private String title;            // Название турнира
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SportType sportType;     // Привязка к виду спорта
    private LocalDate startDate;     // Дата начала
    private LocalDate endDate;       // Дата окончания
    @Column(precision = 19, scale = 2)
    private BigDecimal prizePool;    // Призовой фонд
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "tournament_teams",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<Team> teams = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referee_id")
    private RefereeUser referee;
    @OneToMany(mappedBy = "tournament")
    private List<Match> matches = new ArrayList<>();
    public Tournament() {
    }
    public Tournament(Long id, String title, SportType sportType, LocalDate startDate, LocalDate endDate, BigDecimal prizePool) {
        this.id = id;
        this.title = title;
        this.sportType = sportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.prizePool = prizePool;
    }
    public void addTeam(Team team) {
        if (team == null) {
            throw new ValidationException("Команду нельзя добавить в турнир: объект команды не передан",
                    "Выберите существующую команду для регистрации");
        }
        this.teams.add(team);
    }
    /**
     * Алгоритм проверки окна регистрации на турнир (Аналог проверки дня рождения).
     * Регистрация открыта, если до начала турнира осталось не более 14 дней и он еще не начался.
     */
    public boolean isRegistrationOpen() {
        if (startDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate registrationStart = startDate.minusDays(14);
        return !today.isBefore(registrationStart) && today.isBefore(startDate);
    }
    /**
     * Генерация текстовой турнирной таблицы с использованием циклов и форматирования.
     */
    public String generateStandingsTable() {
        StringBuilder table = new StringBuilder();
        String sportName = (sportType != null) ? sportType.getDisplayName() : "Общий";
        table.append(String.format("=== ТУРНИРНАЯ ТАБЛИЦА: %s [%s] ===\n", title, sportName));
        table.append(String.format("%-5s | %-20s | %-10s\n", "№", "Команда", "Город"));
        table.append("--------------------------------------------------\n");
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            table.append(String.format("%-5d | %-20s | %-10s\n",
                    i + 1, team.getName(), team.getCity()));
        }
        table.append("==================================================\n");
        return table.toString();
    }
    /** Сформировать таблицу по завершённым матчам турнира с подсчётом очков. */
    public String generateStandingsTable(List<Match> matches) {
        // Индексы массива: игры, победы, ничьи, поражения, очки.
        Map<Long, int[]> statsByTeamId = new HashMap<>();
        for (Team team : teams) {
            statsByTeamId.put(team.getId(), new int[5]);
        }
        for (Match match : matches) {
            if (match.getStatus() != MatchStatus.FINISHED) {
                continue;
            }
            Team homeTeam = match.getHomeTeam();
            Team awayTeam = match.getAwayTeam();
            if (homeTeam == null || awayTeam == null) {
                continue;
            }
            int[] homeStats = statsByTeamId.get(homeTeam.getId());
            int[] awayStats = statsByTeamId.get(awayTeam.getId());
            if (homeStats == null || awayStats == null) {
                continue;
            }

            int homeGoals = match.getHomeGoals();
            int awayGoals = match.getAwayGoals();
            homeStats[0]++;
            awayStats[0]++;
            if (homeGoals > awayGoals) {
                homeStats[1]++;
                homeStats[4] += 3;
                awayStats[3]++;
            } else if (awayGoals > homeGoals) {
                awayStats[1]++;
                awayStats[4] += 3;
                homeStats[3]++;
            } else {
                homeStats[2]++;
                awayStats[2]++;
                homeStats[4]++;
                awayStats[4]++;
            }
        }

        List<Team> sortedTeams = new ArrayList<>(teams);
        sortedTeams.sort((first, second) -> Integer.compare(
                statsByTeamId.get(second.getId())[4], statsByTeamId.get(first.getId())[4]));

        StringBuilder table = new StringBuilder();
        String sportName = (sportType != null) ? sportType.getDisplayName() : "Общий";
        table.append(String.format("=== ТУРНИРНАЯ ТАБЛИЦА: %s [%s] ===\n", title, sportName));
        table.append(String.format("%-5s | %-20s | %-15s | %-4s | %-2s | %-2s | %-2s | %-3s\n",
                "№", "Команда", "Город", "И", "В", "Н", "П", "О"));
        table.append("----------------------------------------------------------------------------------\n");
        for (int i = 0; i < sortedTeams.size(); i++) {
            Team team = sortedTeams.get(i);
            int[] stats = statsByTeamId.get(team.getId());
            table.append(String.format("%-5d | %-20s | %-15s | %-4d | %-2d | %-2d | %-2d | %-3d\n",
                    i + 1, team.getName(), team.getCity(),
                    stats[0], stats[1], stats[2], stats[3], stats[4]));
        }
        table.append("==================================================================================\n");
        return table.toString();
    }
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public SportType getSportType() { return sportType; }
    public void setSportType(SportType sportType) { this.sportType = sportType; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getPrizePool() { return prizePool; }
    public void setPrizePool(BigDecimal prizePool) { this.prizePool = prizePool; }
    public List<Team> getTeams() { return teams; }
    public void setTeams(List<Team> teams) { this.teams = teams == null ? new ArrayList<>() : new ArrayList<>(teams); }
    public RefereeUser getReferee() { return referee; }
    public void setReferee(RefereeUser referee) { this.referee = referee; }
    public List<Match> getMatches() { return matches; }
}
