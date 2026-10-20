package com.tournament.service;

import com.tournament.model.enums.UserRole;
import com.tournament.model.user.Admin;
import com.tournament.model.user.Captain;
import com.tournament.model.user.Coach;
import com.tournament.model.user.Manager;
import com.tournament.model.user.Player;
import com.tournament.model.user.Referee;
import com.tournament.model.user.User;
import com.tournament.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Сервис управления пользователями.
 * <p>
 * Отвечает за регистрацию пользователей разных ролей, валидацию учётных
 * данных и поиск по репозиторию. Не содержит логики турниров и матчей.
 */
public class UserService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "Репозиторий пользователей не может быть null");
    }

    public Admin registerAdmin(String email, String password, String firstName,
                               String lastName, String phoneNumber) {
        validateNewUser(email, password);
        Admin admin = new Admin(null, email, password, firstName, lastName, phoneNumber);
        userRepository.save(admin);
        return admin;
    }

    public Manager registerManager(String email, String password, String firstName,
                                   String lastName, String phoneNumber) {
        validateNewUser(email, password);
        Manager manager = new Manager(null, email, password, firstName, lastName, phoneNumber);
        userRepository.save(manager);
        return manager;
    }

    public Player registerPlayer(String email, String password, String firstName,
                                 String lastName, String phoneNumber) {
        validateNewUser(email, password);
        Player player = new Player(null, email, password, firstName, lastName, phoneNumber);
        userRepository.save(player);
        return player;
    }

    public Captain registerCaptain(String email, String password, String firstName,
                                   String lastName, String phoneNumber) {
        validateNewUser(email, password);
        Captain captain = new Captain(null, email, password, firstName, lastName, phoneNumber);
        userRepository.save(captain);
        return captain;
    }

    public Coach registerCoach(String email, String password, String firstName,
                               String lastName, String phoneNumber) {
        validateNewUser(email, password);
        Coach coach = new Coach(null, email, password, firstName, lastName, phoneNumber);
        userRepository.save(coach);
        return coach;
    }

    public Referee registerReferee(String email, String password, String firstName,
                                   String lastName, String phoneNumber) {
        validateNewUser(email, password);
        Referee referee = new Referee(null, email, password, firstName, lastName, phoneNumber);
        userRepository.save(referee);
        return referee;
    }

    /**
     * Находит пользователя по идентификатору.
     *
     * @throws IllegalArgumentException если пользователь не найден
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + id + " не найден"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<User> findByLastName(String lastName) {
        return userRepository.findByLastName(lastName);
    }

    public boolean deleteUser(Long id) {
        return userRepository.deleteById(id);
    }

    public int count() {
        return userRepository.count();
    }

    private void validateNewUser(String email, String password) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Некорректный формат email: " + email);
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Пароль пользователя не может быть пустым");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Пользователь с email " + email + " уже существует");
        }
    }
}
