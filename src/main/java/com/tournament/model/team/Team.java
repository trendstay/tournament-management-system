package com.tournament.model.team;

import com.tournament.model.user.CoachUser;
import com.tournament.model.user.PlayerUser;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс спортивной команды.
 */
public class Team {
    private Long id;      // Первичный ключ
    private String name;  // Название команды
    private String city;  // Город базирования

    private CoachUser coach;
    private PlayerUser captain;
    private List<PlayerUser> players = new ArrayList<>();

    public Team() {}

    public Team(Long id, String name, String city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PlayerUser getCaptain() {
        return captain;
    }

    public void setCaptain(PlayerUser captain) {
        if (captain == null) {
            throw new IllegalArgumentException("Капитан не может быть null");
        }
        if (!players.contains(captain)) {
            players.add(captain);
        }
        if (this.captain != null && this.captain != captain) {
            this.captain.setCaptain(false);
        }
        captain.setCaptain(true);
        this.captain = captain;
    }

    public CoachUser getCoach() { return coach; }
    public void setCoach(CoachUser coach) { this.coach = coach; }
    public List<PlayerUser> getPlayers() { return players; }
    public void setPlayers(List<PlayerUser> players) { this.players = players == null ? new ArrayList<>() : new ArrayList<>(players); }
    public void addPlayer(PlayerUser player) { if (player == null) throw new IllegalArgumentException("Игрок не может быть null"); players.add(player); }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}