package com.tournament.service;

import com.tournament.model.enums.UserRole;
import com.tournament.model.team.Team;
import com.tournament.model.user.AdminUser;
import com.tournament.model.user.CoachUser;
import com.tournament.model.user.PlayerUser;
import com.tournament.model.user.RefereeUser;
import com.tournament.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для сервиса пользователей (UserService).
 */
class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserRepository());
    }

    @Test
    void testRegisterAdmin_ShouldCreateAdminUser() {
        AdminUser admin = userService.registerAdmin("admin@test.com", "pass123", "Иван", "Иванов");

        assertNotNull(admin.getId());
        assertEquals(UserRole.ADMIN, admin.getRole());
        assertTrue(admin.canCreateTournament());
    }

    @Test
    void registerCoach_shouldCreateCoachWithCoachRole() {
        CoachUser coach = userService.registerCoach(
                "coach@test.com", "pass123", "Иван", "Иванов"
        );

        assertNotNull(coach.getId());
        assertEquals(UserRole.COACH, coach.getRole());
        assertFalse(coach.canCreateTournament());
        assertFalse(coach.canManageUsers());
    }

    @Test
    void registerReferee_shouldCreateRefereeWithRefereeRole() {
        RefereeUser referee = userService.registerReferee(
                "referee@test.com", "pass123", "Пётр", "Петров"
        );

        assertNotNull(referee.getId());
        assertEquals(UserRole.REFEREE, referee.getRole());
        assertFalse(referee.canCreateTournament());
        assertFalse(referee.canManageUsers());
    }

    @Test
    void testRegisterPlayer_ShouldCreatePlayer() {
        PlayerUser player = userService.registerPlayer("player@test.com", "pass123", "Петр", "Петров", "+79990000000");

        assertNotNull(player.getId());
        assertEquals(UserRole.PLAYER, player.getRole());
        assertFalse(player.canCreateTournament());
    }

    @Test
    void team_shouldStoreCoachAndPlayers() {
        Team team = new Team();
        CoachUser coach = new CoachUser(null, "coach@test.com", "hash", "Иван", "Иванов");
        PlayerUser player = new PlayerUser(
                null, "player@test.com", "hash", "Пётр", "Петров", "+79990000000"
        );

        team.setCoach(coach);
        team.addPlayer(player);

        assertEquals(coach, team.getCoach());
        assertEquals(1, team.getPlayers().size());
        assertEquals(player, team.getPlayers().get(0));
    }

    @Test
    void testRegister_DuplicateEmail_ShouldThrowException() {
        userService.registerAdmin("test@test.com", "pass123", "A", "B");

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerManager("test@test.com", "pass123", "C", "D");
        });
    }
}