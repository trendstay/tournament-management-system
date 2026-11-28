package com.tournament.security;

import com.tournament.model.user.User;
import com.tournament.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Загрузка учётной записи для Spring Security.
 *
 * <p>В системе нет отдельного логина: именем пользователя служит email.
 * Поиск выполняется существующим методом репозитория
 * {@link UserRepository#findByEmailIgnoreCase(String)} (без учёта регистра).
 * Роль из базы превращается в полномочие вида {@code ROLE_ADMIN}, которое
 * ожидают правила доступа и выражение {@code hasRole('ADMIN')}.</p>
 *
 * <p>Пароль возвращается в том виде, в каком хранится в базе (BCrypt-хеш).
 * Сравнение с введённым паролем выполняет {@code DaoAuthenticationProvider}
 * с помощью {@code BCryptPasswordEncoder}.</p>
 *
 * <p>Лабораторная работа №4: Spring Security, настройка ролей.</p>
 */
@Service
public class TournamentUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public TournamentUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь с email " + email + " не найден", null));
        // Роль хранится в базе (ADMIN, MANAGER, ...), Spring Security ждёт ROLE_ + имя роли.
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}
