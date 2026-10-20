package com.tournament.service;

import com.tournament.model.team.Team;
import com.tournament.model.user.Captain;
import com.tournament.model.user.Coach;
import com.tournament.model.user.Player;
import com.tournament.repository.TeamRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис управления командами: создание, назначение тренера и капитана,
 * работа с составом игроков.
 */
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = Objects.requireNonNull(teamRepository, "Репозиторий команд не может быть null");
    }

    /**
     * Создаёт команду с уникальным названием.
     */
    public Team createTeam(String name, String city) {
        if (name != null && teamRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Команда с названием '" + name + "' уже существует");
        }
        Team team = new Team(null, name, city);
        teamRepository.save(team);
        return team;
    }

    /**
     * Назначает команде тренера.
     */
    public Team assignCoach(Long teamId, Coach coach) {
        Team team = getTeam(teamId);
        team.setCoach(Objects.requireNonNull(coach, "Тренер не может быть null"));
        return teamRepository.save(team);
    }

    /**
     * Назначает команде капитана. Капитан автоматически попадает в состав.
     */
    public Team appointCaptain(Long teamId, Captain captain) {
        Team team = getTeam(teamId);
        Objects.requireNonNull(captain, "Капитан не может быть null");
        team.setCaptain(captain);
        return teamRepository.save(team);
    }

    /**
     * Добавляет игрока в состав команды.
     */
    public Team addPlayer(Long teamId, Player player) {
        Team team = getTeam(teamId);
        team.addPlayer(player);
        return teamRepository.save(team);
    }

    /**
     * Удаляет игрока из состава команды по email.
     */
    public Team removePlayer(Long teamId, String email) {
        Team team = getTeam(teamId);
        team.removePlayerByEmail(email);
        return teamRepository.save(team);
    }

    /**
     * Находит команду по идентификатору.
     *
     * @throws IllegalArgumentException если команда не найдена
     */
    public Team findById(Long id) {
        return getTeam(id);
    }

    public Optional<Team> findByName(String name) {
        return teamRepository.findByName(name);
    }

    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public List<Team> findByCity(String city) {
        return teamRepository.findByCity(city);
    }

    public boolean deleteTeam(Long id) {
        return teamRepository.deleteById(id);
    }

    private Team getTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Команда с ID " + id + " не найдена"));
    }
}
