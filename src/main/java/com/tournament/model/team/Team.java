package com.tournament.model.team;

import com.tournament.model.Identifiable;
import com.tournament.model.user.Captain;
import com.tournament.model.user.Coach;
import com.tournament.model.user.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Спортивная команда.
 * <p>
 * Хранит название, город, тренера, капитана и список игроков.
 * Название команды является её бизнес-ключом (в ER-модели — первичный ключ),
 * поэтому две команды с одинаковым названием считаются одной и той же командой.
 */
public class Team implements Identifiable {

    private Long id;
    private final String name;
    private String city;
    private Coach coach;
    private Captain captain;
    private final List<Player> players = new ArrayList<>();

    public Team(String name, String city) {
        this(null, name, city);
    }

    public Team(Long id, String name, String city) {
        this.id = id;
        this.name = requireName(name);
        this.city = city;
    }

    /**
     * Добавляет игрока в состав команды.
     *
     * @throws IllegalArgumentException если игрок равен {@code null}
     *                                  или игрок с таким email уже есть в команде
     */
    public void addPlayer(Player player) {
        Objects.requireNonNull(player, "Игрок не может быть null");
        if (findPlayerByEmail(player.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Игрок с email " + player.getEmail() + " уже состоит в команде " + name);
        }
        players.add(player);
    }

    /**
     * Удаляет игрока из состава команды.
     *
     * @return {@code true}, если игрок был удалён
     */
    public boolean removePlayer(Player player) {
        if (player == null) {
            return false;
        }
        return removePlayerByEmail(player.getEmail());
    }

    /**
     * Удаляет игрока из состава команды по email.
     *
     * @return {@code true}, если игрок был удалён
     */
    public boolean removePlayerByEmail(String email) {
        if (email == null) {
            return false;
        }
        return players.removeIf(player -> player.getEmail().equalsIgnoreCase(email));
    }

    /**
     * Ищет игрока команды по email.
     */
    public Optional<Player> findPlayerByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        for (Player player : players) {
            if (player.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }

    /**
     * Ищет игрока команды по имени и фамилии.
     */
    public Optional<Player> findPlayerByFullName(String firstName, String lastName) {
        for (Player player : players) {
            if (Objects.equals(player.getFirstName(), firstName)
                    && Objects.equals(player.getLastName(), lastName)) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }

    /**
     * Заменяет состав команды. Капитан автоматически остаётся в списке игроков.
     */
    public void setPlayers(List<Player> newPlayers) {
        players.clear();
        if (newPlayers != null) {
            for (Player player : newPlayers) {
                addPlayer(player);
            }
        }
        if (captain != null && findPlayerByEmail(captain.getEmail()).isEmpty()) {
            addPlayer(captain);
        }
    }

    /**
     * Назначает капитана команды. Капитан автоматически добавляется в состав,
     * если его там ещё нет.
     */
    public void setCaptain(Captain captain) {
        this.captain = captain;
        if (captain != null && findPlayerByEmail(captain.getEmail()).isEmpty()) {
            addPlayer(captain);
        }
    }

    /**
     * Команда готова к участию в турнире, если у неё есть тренер, капитан
     * и хотя бы один игрок.
     */
    public boolean isReady() {
        return coach != null && captain != null && !players.isEmpty();
    }

    public int getPlayersCount() {
        return players.size();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public Captain getCaptain() {
        return captain;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название команды не может быть пустым");
        }
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Team)) {
            return false;
        }
        Team team = (Team) other;
        return name.equalsIgnoreCase(team.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase(Locale.ROOT).hashCode();
    }

    @Override
    public String toString() {
        return "Team{name='" + name + "', city='" + city + "', players=" + players.size() + "}";
    }
}
