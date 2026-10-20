package com.tournament.service;

import com.tournament.model.enums.UserRole;
import com.tournament.model.user.Admin;
import com.tournament.model.user.Captain;
import com.tournament.model.user.Coach;
import com.tournament.model.user.Manager;
import com.tournament.model.user.Player;
import com.tournament.model.user.Referee;
import com.tournament.model.user.User;
import com.tournament.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit-тесты сервиса пользователей.
 */
class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserRepository());
    }

    @Test
    void registerAdmin_shouldCreateAdminWithFullPermissions() {
        Admin admin = userService.registerAdmin(
                "admin@test.com", "pass123", "Иван", "Иванов", "+79990000001");

        assertNotNull(admin.getId());
        assertEquals(UserRole.ADMIN, admin.getRole());
        assertTrue(admin.canCreateTournament());
        assertTrue(admin.canManageUsers());
        assertEquals("Иван Иванов", admin.getFullName());
    }

    @Test
    void registerManager_shouldCreateManagerWhoCannotManageUsers() {
        Manager manager = userService.registerManager(
                "manager@test.com", "pass123", "Пётр", "Петров", "+79990000002");

        assertEquals(UserRole.MANAGER, manager.getRole());
        assertTrue(manager.canCreateTournament());
        assertFalse(manager.canManageUsers());
    }

    @Test
    void registerCoach_shouldCreateCoachWhoCanManageTeam() {
        Coach coach = userService.registerCoach(
                "coach@test.com", "pass123", "Олег", "Олегов", "+79990000003");

        assertEquals(UserRole.COACH, coach.getRole());
        assertTrue(coach.canManageTeam());
        assertFalse(coach.canCreateTournament());
    }

    @Test
    void registerReferee_shouldCreateRefereeWhoCanJudgeMatches() {
        Referee referee = userService.registerReferee(
                "referee@test.com", "pass123", "Сергей", "Сергеев", "+79990000004");

        assertEquals(UserRole.REFEREE, referee.getRole());
        assertTrue(referee.canRefereeMatches());
        assertFalse(referee.canManageTeam());
    }

    @Test
    void registerPlayer_shouldCreatePlayerWithLimitedPermissions() {
        Player player = userService.registerPlayer(
                "player@test.com", "pass123", "Алексей", "Алексеев", "+79990000005");

        assertEquals(UserRole.PLAYER, player.getRole());
        assertFalse(player.canCreateTournament());
        assertFalse(player.canManageUsers());
        assertFalse(player.canManageTeam());
        assertFalse(player.canRefereeMatches());
    }

    @Test
    void registerCaptain_shouldCreateCaptainAsPlayerWithTeamRights() {
        Captain captain = userService.registerCaptain(
                "captain@test.com", "pass123", "Артём", "Артёмов", "+79990000006");

        assertEquals(UserRole.CAPTAIN, captain.getRole());
        assertTrue(captain instanceof Player);
        assertTrue(captain.canManageTeam());
        assertFalse(captain.canCreateTournament());
    }

    @Test
    void register_duplicateEmailInDifferentCase_shouldThrowException() {
        userService.registerAdmin("duplicate@test.com", "pass123", "Иван", "Иванов", "+79990000001");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerManager(
                        "DUPLICATE@TEST.COM", "pass123", "Пётр", "Петров", "+79990000002"));

        assertTrue(exception.getMessage().contains("уже существует"));
    }

    @Test
    void register_invalidEmail_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerPlayer(
                        "invalid-email", "pass123", "Иван", "Иванов", "+79990000001"));

        assertTrue(exception.getMessage().contains("Некорректный формат email"));
    }

    @Test
    void register_blankPassword_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerAdmin(
                        "admin@test.com", "   ", "Иван", "Иванов", "+79990000001"));

        assertTrue(exception.getMessage().contains("Пароль"));
    }

    @Test
    void findById_whenUserDoesNotExist_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.findById(999L));

        assertTrue(exception.getMessage().contains("не найден"));
    }

    @Test
    void findByRole_shouldReturnOnlyUsersWithRequestedRole() {
        userService.registerPlayer("player1@test.com", "pass", "Иван", "Иванов", "+79990000001");
        userService.registerPlayer("player2@test.com", "pass", "Пётр", "Петров", "+79990000002");
        userService.registerReferee("referee@test.com", "pass", "Сергей", "Сергеев", "+79990000003");

        List<User> players = userService.findByRole(UserRole.PLAYER);

        assertEquals(2, players.size());
        assertTrue(players.stream().allMatch(user -> user.getRole() == UserRole.PLAYER));
    }

    @Test
    void findByLastName_shouldIgnoreCase() {
        userService.registerPlayer("player@test.com", "pass", "Иван", "Сидоров", "+79990000001");

        List<User> found = userService.findByLastName("сидоров");

        assertEquals(1, found.size());
        assertEquals("player@test.com", found.get(0).getEmail());
    }

    @Test
    void deleteUser_shouldRemoveUserFromRepository() {
        Admin admin = userService.registerAdmin(
                "admin@test.com", "pass123", "Иван", "Иванов", "+79990000001");

        assertTrue(userService.deleteUser(admin.getId()));
        assertThrows(IllegalArgumentException.class, () -> userService.findById(admin.getId()));
        assertEquals(0, userService.count());
    }
}
