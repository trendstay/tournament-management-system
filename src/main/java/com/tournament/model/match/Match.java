package com.tournament.model.match;

import com.tournament.model.enums.MatchStatus;
import com.tournament.model.team.Team;
import java.time.LocalDateTime;

/**
 * Класс спортивного матча с алгоритмами обработки данных.
 */
public class Match {
    private Long id;        // Первичный ключ
    private Team homeTeam; // Хозяева
    private Team awayTeam; // Гости
    private String score;  // Формат "X:Y", например "2:1"
    private MatchStatus status;  // Текущий статус
    private LocalDateTime matchDate;  // Дата и время проведения

    public Match() {
        this.status = MatchStatus.SCHEDULED;
        this.score = "0:0";
    }

    public Match(Long id, Team homeTeam, Team awayTeam, LocalDateTime matchDate) {
        this();
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
    }

    /**
     * Валидация и обновление счета матча с помощью преобразования строк.
     */
    public void updateScore(String newScore) {
        if (newScore == null || !newScore.matches("\\d+:\\d+")) {
            throw new IllegalArgumentException("Некорректный формат счета! Ожидается 'X:Y', например '2:1'");
        }
        this.score = newScore;
    }

    /**
     * Преобразование строки счета для получения голов/очков хозяев.
     */
    public int getHomeGoals() {
        String[] parts = score.split(":");
        return Integer.parseInt(parts[0].trim());
    }

    /**
     * Преобразование строки счета для получения голов/очков гостей.
     */
    public int getAwayGoals() {
        String[] parts = score.split(":");
        return Integer.parseInt(parts[1].trim());
    }

    /**
     * Логический алгоритм определения победителя.
     */
    public Team getWinner() {
        if (status != MatchStatus.FINISHED) {
            return null;
        }
        int homeGoals = getHomeGoals();
        int awayGoals = getAwayGoals();

        if (homeGoals > awayGoals) {
            return homeTeam;
        } else if (awayGoals > homeGoals) {
            return awayTeam;
        }
        return null; // Ничья
    }

    /**
     * Алгоритм формирования форматированного протокола матча.
     */
    public String generateMatchReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== ОФИЦИАЛЬНЫЙ ПРОТОКОЛ МАТЧА №").append(id != null ? id : "Б/Н").append(" ===\n");
        report.append(String.format("Дата проведения: %s\n", matchDate));
        report.append(String.format("Участники: %s vs %s\n",
                homeTeam != null ? homeTeam.getName() : "Хозяева",
                awayTeam != null ? awayTeam.getName() : "Гости"));
        report.append(String.format("Текущий счет: %s\n", score));
        report.append(String.format("Статус: %s\n", status));

        if (status == MatchStatus.FINISHED) {
            Team winner = getWinner();
            if (winner != null) {
                report.append(String.format("Результат: Победитель — %s\n", winner.getName()));
            } else {
                report.append("Результат: Зафиксирована ничья\n");
            }
        }
        report.append("========================================\n");
        return report.toString();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Team getHomeTeam() { return homeTeam; }
    public void setHomeTeam(Team homeTeam) { this.homeTeam = homeTeam; }

    public Team getAwayTeam() { return awayTeam; }
    public void setAwayTeam(Team awayTeam) { this.awayTeam = awayTeam; }

    public String getScore() { return score; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public LocalDateTime getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }
}