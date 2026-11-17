package com.tournament.model.tournament;
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
import java.util.List;
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
            throw new IllegalArgumentException("Команда не может быть null");
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
