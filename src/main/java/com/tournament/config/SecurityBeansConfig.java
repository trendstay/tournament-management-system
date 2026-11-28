package com.tournament.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Настройка хеширования паролей (лабораторная работа №4).
 *
 * <p>Пароль пользователя никогда не хранится в открытом виде: в поле
 * {@code password_hash} записывается BCrypt-хеш, который начинается с {@code $2a$}.
 * Проверка пароля при входе выполняется методом {@link PasswordEncoder#matches}.</p>
 *
 * <p>Аннотация {@link EnableMethodSecurity} включает проверку прав,
 * объявленных над методами контроллеров через {@code @PreAuthorize}.</p>
 */
@Configuration
@EnableMethodSecurity
public class SecurityBeansConfig {

    /** Кодировщик паролей: BCrypt со стандартной для Spring Security «силой» 10. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
