package com.tournament.model.team;
import com.tournament.exception.ValidationException;
import com.tournament.model.tournament.Tournament;
import com.tournament.model.user.CoachUser;
import com.tournament.model.user.PlayerUser;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
/**
 * Класс спортивной команды.
 * Хранится в таблице {@code teams}, игроки связаны через {@code PlayerUser.team}.
 */
@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;  // Название команды
    private String city;  // Город базирования
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id")
    private CoachUser coach;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "captain_id")
    private PlayerUser captain;
    @OneToMany(mappedBy = "team", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<PlayerUser> players = new ArrayList<>();
    @ManyToMany(mappedBy = "teams")
    private List<Tournament> tournaments = new ArrayList<>();
    public Team() {
    }
    public Team(Long id, String name, String city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public PlayerUser getCaptain() {
        return captain;
    }
    public void setCaptain(PlayerUser captain) {
        if (captain == null) {
            throw new ValidationException("Капитана нельзя назначить: объект игрока не передан",
                    "Передайте существующего игрока, который входит в состав команды");
        }
        if (!players.contains(captain)) {
            players.add(captain);
        }
        if (this.captain != null && this.captain != captain) {
            this.captain.setCaptain(false);
        }
        captain.setCaptain(true);
        captain.setTeam(this);
        this.captain = captain;
    }
    public CoachUser getCoach() { return coach; }
    public void setCoach(CoachUser coach) { this.coach = coach; }
    public List<PlayerUser> getPlayers() { return players; }
    public void setPlayers(List<PlayerUser> players) {
        this.players = players == null ? new ArrayList<>() : new ArrayList<>(players);
    }
    public void addPlayer(PlayerUser player) {
        if (player == null) {
            throw new ValidationException("Игрока нельзя добавить: объект игрока не передан",
                    "Укажите существующего игрока команды");
        }
        player.setTeam(this);
        if (!players.contains(player)) {
            players.add(player);
        }
    }
    public void removePlayer(PlayerUser player) {
        if (player == null) {
            return;
        }
        if (this.captain != null && this.captain.equals(player)) {
            clearCaptain();
        }
        player.setTeam(null);
        players.remove(player);
    }
    /** Снять с игрока полномочия капитана (без исключения, в отличие от setCaptain(null)). */
    public void clearCaptain() {
        if (this.captain != null) {
            this.captain.setCaptain(false);
            this.captain = null;
        }
    }
    public List<Tournament> getTournaments() { return tournaments; }
}
