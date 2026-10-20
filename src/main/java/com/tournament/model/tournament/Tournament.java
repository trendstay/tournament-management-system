package com.tournament.model.tournament;

import com.tournament.model.Identifiable;
import com.tournament.model.enums.SportType;
import com.tournament.model.team.Team;
import com.tournament.model.user.Referee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Спортивный турнир.
 * <p>
 * Турнир относится к одному виду спорта, проводится в заданные даты,
 * имеет призового фонд, главного судью и набор участвующих команд.
 * Команда не может быть зарегистрирована в одном турнире дважды,
 * поэтому команды хранятся в множестве.
 */
public class Tournament implements Identifiable {

    private static final int REGISTRATION_DAYS_BEFORE_START = 14;

    private Long id;
    private String title;
    private SportType sportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal prizePool;
    private Referee referee;
    private final Set<Team> teams = new LinkedHashSet<>();

    public Tournament(String title, SportType sportType, LocalDate startDate,
                      LocalDate endDate, BigDecimal prizePool) {
        this(null, title, sportType, startDate, endDate, prizePool, null);
    }

    public Tournament(Long id, String title, SportType sportType, LocalDate startDate,
                      LocalDate endDate, BigDecimal prizePool) {
        this(id, title, sportType, startDate, endDate, prizePool, null);
    }

    public Tournament(Long id, String title, SportType sportType, LocalDate startDate,
                      LocalDate endDate, BigDecimal prizePool, Referee referee) {
        this.id = id;
        setTitle(title);
        this.sportType = Objects.requireNonNull(sportType, "Вид спорта не может быть null");
        setStartDate(startDate);
        setEndDate(endDate);
        setPrizePool(prizePool);
        this.referee = referee;
    }

    /**
     * Регистрирует команду на турнир.
     *
     * @throws IllegalArgumentException если команда {@code null} или уже зарегистрирована
     */
    public void addTeam(Team team) {
        Objects.requireNonNull(team, "Команда не может быть null");
        if (!teams.add(team)) {
            throw new IllegalArgumentException(
                    "Команда '" + team.getName() + "' уже зарегистрирована в турнире '" + title + "'");
        }
    }

    /**
     * Снимает команду с турнира.
     *
     * @return {@code true}, если команда была зарегистрирована и удалена
     */
    public boolean removeTeam(Team team) {
        if (team == null) {
            return false;
        }
        return teams.remove(team);
    }

    /**
     * Ищет команду турнира по названию.
     */
    public Optional<Team> findTeamByName(String teamName) {
        if (teamName == null) {
            return Optional.empty();
        }
        for (Team team : teams) {
            if (team.getName().equalsIgnoreCase(teamName)) {
                return Optional.of(team);
            }
        }
        return Optional.empty();
    }

    /**
     * Регистрация открыта, если до начала турнира осталось не более 14 дней
     * и турнир ещё не начался.
     */
    public boolean isRegistrationOpen() {
        if (startDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate registrationStart = startDate.minusDays(REGISTRATION_DAYS_BEFORE_START);
        return !today.isBefore(registrationStart) && today.isBefore(startDate);
    }

    /**
     * Формирует текстовую турнирную таблицу с использованием цикла и
     * форматирования строк.
     */
    public String generateStandingsTable() {
        StringBuilder table = new StringBuilder();
        String sportName = sportType == null ? "Общий" : sportType.getDisplayName();
        table.append(String.format("=== ТУРНИРНАЯ ТАБЛИЦА: %s [%s] ===%n", title, sportName));
        table.append(String.format("%-5s | %-20s | %-16s | %-6s%n", "№", "Команда", "Город", "Игроки"));
        table.append("------------------------------------------------------------------\n");

        int position = 1;
        for (Team team : teams) {
            table.append(String.format("%-5d | %-20s | %-16s | %-6d%n",
                    position, team.getName(), team.getCity(), team.getPlayersCount()));
            position++;
        }
        table.append("==================================================================\n");
        return table.toString();
    }

    /**
     * Назначает главного судью турнира.
     */
    public void setReferee(Referee referee) {
        this.referee = referee;
    }

    public boolean hasReferee() {
        return referee != null;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название турнира не может быть пустым");
        }
        this.title = title;
    }

    public SportType getSportType() {
        return sportType;
    }

    public void setSportType(SportType sportType) {
        this.sportType = Objects.requireNonNull(sportType, "Вид спорта не может быть null");
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = Objects.requireNonNull(startDate, "Дата начала турнира не может быть null");
        validateDates();
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = Objects.requireNonNull(endDate, "Дата окончания турнира не может быть null");
        validateDates();
    }

    public BigDecimal getPrizePool() {
        return prizePool;
    }

    public void setPrizePool(BigDecimal prizePool) {
        BigDecimal value = prizePool == null ? BigDecimal.ZERO : prizePool;
        if (value.signum() < 0) {
            throw new IllegalArgumentException("Призовой фонд не может быть отрицательным");
        }
        this.prizePool = value;
    }

    public Referee getReferee() {
        return referee;
    }

    public Set<Team> getTeams() {
        return Collections.unmodifiableSet(teams);
    }

    public int getTeamsCount() {
        return teams.size();
    }

    private void validateDates() {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Дата окончания турнира не может быть раньше даты начала");
        }
    }

    @Override
    public String toString() {
        return "Tournament{title='" + title + "', sportType=" + sportType
                + ", startDate=" + startDate + ", teams=" + teams.size() + "}";
    }
}
