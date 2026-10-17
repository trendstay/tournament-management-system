package com.tournament.service;

import com.tournament.model.enums.UserRole;
import com.tournament.model.user.AdminUser;
import com.tournament.model.user.PlayerUser;
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
    void testRegisterPlayer_ShouldCreatePlayer() {
        PlayerUser customer = userService.registerPlayer("player@test.com", "pass123", "Петр", "Петров", "+79990000000");

        assertNotNull(customer.getId());
        assertEquals(UserRole.PLAYER, customer.getRole());
        assertFalse(customer.canCreateTournament());
    }

    @Test
    void testRegister_DuplicateEmail_ShouldThrowException() {
        userService.registerAdmin("test@test.com", "pass123", "A", "B");

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerManager("test@test.com", "pass123", "C", "D");
        });
    }
}