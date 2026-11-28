package com.tournament.security;

import com.tournament.model.user.AdminUser;
import com.tournament.model.user.CoachUser;
import com.tournament.model.user.PlayerUser;
import com.tournament.model.user.User;
import com.tournament.support.AbstractRepositoryMockSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Тесты загрузки пользователя для Spring Security (лабораторная работа №4).
 * База данных не используется — репозиторий подменён Mockito-моком.
 */
@DisplayName("UserDetailsService: загрузка пользователя по email")
class TournamentUserDetailsServiceTest extends AbstractRepositoryMockSupport {

    @Autowired
    private TournamentUserDetailsService userDetailsService;

    @Test
    @DisplayName("роль ADMIN превращается в полномочие ROLE_ADMIN, имя входа — email")
    void loadUserByUsername_shouldReturnRoleAuthority() {
        User admin = new AdminUser(1L, "admin@test.com", "$2a$10$hash", "Иван", "Иванов");
        when(userRepository.findByEmailIgnoreCase("admin@test.com")).thenReturn(Optional.of(admin));

        UserDetails details = userDetailsService.loadUserByUsername("admin@test.com");

        assertEquals("admin@test.com", details.getUsername());
        assertEquals("$2a$10$hash", details.getPassword());
        List<String> authorities = details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        assertEquals(List.of("ROLE_ADMIN"), authorities);
        assertTrue(details.isEnabled());
    }

    @Test
    @DisplayName("роль COACH превращается в ROLE_COACH")
    void loadUserByUsername_coach_shouldReturnCoachAuthority() {
        User coach = new CoachUser(2L, "coach@test.com", "$2a$10$hash", "Сергей", "Тренеров");
        when(userRepository.findByEmailIgnoreCase("coach@test.com")).thenReturn(Optional.of(coach));

        UserDetails details = userDetailsService.loadUserByUsername("coach@test.com");

        assertEquals("ROLE_COACH", details.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @DisplayName("игрок-капитан получает полномочие ROLE_CAPTAIN, обычный игрок — ROLE_PLAYER")
    void loadUserByUsername_playerRoles_shouldReturnCaptainOrPlayer() {
        PlayerUser captain = new PlayerUser(3L, "captain@test.com", "$2a$10$hash", "Пётр", "Петров", null);
        captain.setCaptain(true);
        when(userRepository.findByEmailIgnoreCase("captain@test.com")).thenReturn(Optional.of(captain));

        UserDetails details = userDetailsService.loadUserByUsername("captain@test.com");

        assertEquals("ROLE_CAPTAIN", details.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @DisplayName("неизвестный email приводит к UsernameNotFoundException")
    void loadUserByUsername_whenMissing_shouldThrowException() {
        when(userRepository.findByEmailIgnoreCase("nobody@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nobody@test.com"));
    }
}
