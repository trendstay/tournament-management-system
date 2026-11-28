package com.tournament.security;

import com.tournament.dto.user.UserRequest;
import com.tournament.model.enums.UserRole;
import com.tournament.model.user.User;
import com.tournament.support.AbstractRepositoryMockSupport;
import com.tournament.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты хеширования паролей (лабораторная работа №4).
 *
 * <p>Проверяется, что открытый пароль не попадает в сущность: в поле
 * {@code passwordHash} сохраняется BCrypt-хеш, который начинается с {@code $2a$},
 * и что при обновлении без пароля прежний хеш не меняется.</p>
 */
@DisplayName("Хеширование пароля: BCrypt в сервисе пользователей")
class PasswordHashingTest extends AbstractRepositoryMockSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("create: в базе сохраняется BCrypt-хеш, а не открытый пароль")
    void create_shouldStoreBcryptHash() {
        when(userRepository.findByEmailIgnoreCase("hash@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserRequest request = new UserRequest("hash@test.com", "pass123", "Иван", "Иванов",
                UserRole.ADMIN, null, null);

        userService.create(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        String stored = captor.getValue().getPasswordHash();
        assertNotEquals("pass123", stored);
        assertTrue(stored.startsWith("$2a$"), "Ожидался BCrypt-хеш, получено: " + stored);
        assertTrue(passwordEncoder.matches("pass123", stored));
    }

    @Test
    @DisplayName("update: пустой пароль не меняет сохранённый хеш")
    void update_withoutPassword_shouldKeepHash() {
        String existingHash = passwordEncoder.encode("pass123");
        User admin = new com.tournament.model.user.AdminUser(1L, "admin@test.com", existingHash,
                "Иван", "Иванов");
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findByEmailIgnoreCase("admin@test.com")).thenReturn(Optional.of(admin));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserRequest request = new UserRequest("admin@test.com", null, "Пётр", "Петров",
                UserRole.ADMIN, null, null);

        userService.update(1L, request);

        assertEquals(existingHash, admin.getPasswordHash());
        assertEquals("Пётр", admin.getFirstName());
    }

    @Test
    @DisplayName("update: переданный пароль сохраняется новым хешем")
    void update_withPassword_shouldStoreNewHash() {
        String existingHash = passwordEncoder.encode("pass123");
        User admin = new com.tournament.model.user.AdminUser(1L, "admin@test.com", existingHash,
                "Иван", "Иванов");
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findByEmailIgnoreCase("admin@test.com")).thenReturn(Optional.of(admin));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserRequest request = new UserRequest("admin@test.com", "newPass456", "Иван", "Иванов",
                UserRole.ADMIN, null, null);

        userService.update(1L, request);

        assertNotEquals(existingHash, admin.getPasswordHash());
        assertTrue(passwordEncoder.matches("newPass456", admin.getPasswordHash()));
    }
}
