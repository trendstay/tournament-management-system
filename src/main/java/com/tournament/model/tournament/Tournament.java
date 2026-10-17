package com.tournament.model.tournament;

import com.tournament.model.enums.SportType;
import com.tournament.model.team.Team;
import com.tournament.model.user.RefereeUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * Класс спортивного турнира.
 */
public class Tournament {
    private Long id;                 // Первичный ключ
    private String title;            // Название турнира
    private SportType sportType;     // Привязка к виду спорта
    private LocalDate startDate;     // Дата начала
    private LocalDate endDate;       // Дата окончания
    private BigDecimal prizePool;    // Призовой фонд
    private List<Team> teams = new ArrayList<>();
    private RefereeUser referee;

    public Tournament() {}

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
    public RefereeUser getReferee() { return referee; }
    public void setReferee(RefereeUser referee) { this.referee = referee; }
}