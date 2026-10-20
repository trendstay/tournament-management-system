package com.tournament.model.match;

import com.tournament.model.Identifiable;
import com.tournament.model.enums.MatchStatus;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import com.tournament.model.user.Referee;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Спортивный матч.
 * <p>
 * Матч проводится в рамках турнира между двумя разными командами,
 * обслуживается судьёй и имеет счёт и статус. Счёт хранится строкой
 * формата {@code "X:Y"} и разбирается на составляющие при обработке.
 */
public class Match implements Identifiable {

    private static final String DEFAULT_SCORE = "0:0";
    private static final String SCORE_PATTERN = "\\d+:\\d+";

    private Long id;
    private Team homeTeam;
    private Team awayTeam;
    private Referee referee;
    private Tournament tournament;
    private String score;
    private MatchStatus status;
    private LocalDateTime matchDate;

    public Match(Team homeTeam, Team awayTeam, LocalDateTime matchDate,
                 Tournament tournament, Referee referee) {
        this(null, homeTeam, awayTeam, matchDate, tournament, referee);
    }

    public Match(Long id, Team homeTeam, Team awayTeam, LocalDateTime matchDate) {
        this(id, homeTeam, awayTeam, matchDate, null, null);
    }

    public Match(Long id, Team homeTeam, Team awayTeam, LocalDateTime matchDate,
                 Tournament tournament, Referee referee) {
        this.id = id;
        this.homeTeam = Objects.requireNonNull(homeTeam, "Домашняя команда не может быть null");
        this.awayTeam = Objects.requireNonNull(awayTeam, "Гостевая команда не может быть null");
        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException("Команда не может проводить матч сама с собой");
        }
        this.matchDate = Objects.requireNonNull(matchDate, "Дата матча не может быть null");
        this.tournament = tournament;
        this.referee = referee;
        this.score = DEFAULT_SCORE;
        this.status = MatchStatus.SCHEDULED;
    }

    /**
     * Валидирует и обновляет счёт матча.
     *
     * @param newScore строка формата {@code "X:Y"}, например {@code "2:1"}
     * @throws IllegalArgumentException если формат счёта некорректный
     */
    public void updateScore(String newScore) {
        if (newScore == null || !newScore.matches(SCORE_PATTERN)) {
            throw new IllegalArgumentException(
                    "Некорректный формат счёта. Ожидается 'X:Y', например '2:1'");
        }
        this.score = newScore;
    }

    /**
     * Количество голов (очков) домашней команды.
     */
    public int getHomeGoals() {
        String[] parts = score.split(":");
        return Integer.parseInt(parts[0].trim());
    }

    /**
     * Количество голов (очков) гостевой команды.
     */
    public int getAwayGoals() {
        String[] parts = score.split(":");
        return Integer.parseInt(parts[1].trim());
    }

    /**
     * Победитель матча или {@code null}, если матч не завершён или зафиксирована ничья.
     */
    public Team getWinner() {
        if (status != MatchStatus.FINISHED) {
            return null;
        }
        int homeGoals = getHomeGoals();
        int awayGoals = getAwayGoals();
        if (homeGoals > awayGoals) {
            return homeTeam;
        }
        if (awayGoals > homeGoals) {
            return awayTeam;
        }
        return null;
    }

    /**
     * Завершён ли матч ничьей.
     */
    public boolean isDraw() {
        return status == MatchStatus.FINISHED && getHomeGoals() == getAwayGoals();
    }

    /**
     * Формирует официальный протокол матча.
     */
    public String generateMatchReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== ОФИЦИАЛЬНЫЙ ПРОТОКОЛ МАТЧА №")
                .append(id != null ? id : "Б/Н")
                .append(" ===\n");
        if (tournament != null) {
            report.append(String.format("Турнир: %s%n", tournament.getTitle()));
        }
        report.append(String.format("Дата проведения: %s%n", matchDate));
        report.append(String.format("Участники: %s vs %s%n", homeTeam.getName(), awayTeam.getName()));
        report.append(String.format("Судья: %s%n",
                referee != null ? referee.getFullName() : "не назначен"));
        report.append(String.format("Текущий счёт: %s%n", score));
        report.append(String.format("Статус: %s%n", status.getDisplayName()));

        if (status == MatchStatus.FINISHED) {
            Team winner = getWinner();
            if (winner != null) {
                report.append(String.format("Результат: победитель — %s%n", winner.getName()));
            } else {
                report.append("Результат: зафиксирована ничья\n");
            }
        }
        report.append("========================================\n");
        return report.toString();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = Objects.requireNonNull(homeTeam, "Домашняя команда не может быть null");
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = Objects.requireNonNull(awayTeam, "Гостевая команда не может быть null");
    }

    public Referee getReferee() {
        return referee;
    }

    public void setReferee(Referee referee) {
        this.referee = referee;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public String getScore() {
        return score;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = Objects.requireNonNull(status, "Статус матча не может быть null");
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = Objects.requireNonNull(matchDate, "Дата матча не может быть null");
    }

    @Override
    public String toString() {
        return "Match{id=" + id + ", " + homeTeam.getName() + " vs " + awayTeam.getName()
                + ", score='" + score + "', status=" + status + "}";
    }
}
