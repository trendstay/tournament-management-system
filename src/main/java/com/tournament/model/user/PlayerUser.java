package com.tournament.model.user;
import com.tournament.model.enums.UserRole;
import com.tournament.model.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
/**
 * Игрок турнира и пользователь системы.
 * Имеет обычные права базового пользователя системы.
 * Может быть капитаном команды (роль CAPTAIN) и принадлежать команде.
 */
@Entity
@DiscriminatorValue("PLAYER")
public class PlayerUser extends User {
    @Column(length = 50)
    private String phoneNumber;
    /**
     * Признак капитана. Обёртка Boolean (а не примитив), так как колонка относится
     * только к подклассу PlayerUser и у пользователей других ролей должна быть NULL.
     */
    private Boolean captain;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    public PlayerUser() {
        super();
        setRole(UserRole.PLAYER);
    }
    public PlayerUser(Long id, String email, String passwordHash, String firstName, String lastName, String phoneNumber) {
        super(id, email, passwordHash, firstName, lastName, UserRole.PLAYER);
        this.phoneNumber = phoneNumber;
    }
    public boolean isCaptain() {
        return Boolean.TRUE.equals(captain);
    }
    public void setCaptain(boolean captain) {
        this.captain = captain;
        setRole(captain ? UserRole.CAPTAIN : UserRole.PLAYER);
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public Team getTeam() {
        return team;
    }
    public void setTeam(Team team) {
        this.team = team;
    }
    @Override
    public boolean canCreateTournament() {
        return false;
    }
    @Override
    public boolean canManageUsers() {
        return false;
    }
}
