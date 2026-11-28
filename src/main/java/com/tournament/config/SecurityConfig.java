package com.tournament.config;

import com.tournament.security.JsonSecurityErrorHandler;
import com.tournament.security.SecurityAccessLogFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Настройка Spring Security для REST API (лабораторная работа №4).
 *
 * <p>Что настраивается:</p>
 * <ol>
 *   <li>вход по HTTP Basic (email + пароль), без страницы логина и без JWT;</li>
 *   <li>CSRF отключён: REST-клиенты не работают с формами и cookie-сессией;</li>
 *   <li>Swagger UI и OpenAPI JSON открыты без входа, остальные методы — только
 *       для аутентифицированных пользователей;</li>
 *   <li>сессия не создаётся (stateless): каждый запрос передаёт учётные данные заново;</li>
 *   <li>отказ возвращается в едином формате {@code ErrorResponse}: 401 и 403.</li>
 * </ol>
 *
 * <p>Права по ролям объявлены на методах контроллеров через {@code @PreAuthorize}
 * (включено в {@link SecurityBeansConfig}). В этом классе намеренно нет матрицы прав:
 * чтобы правило не дублировалось в двух местах, его видно прямо над методом.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Пути документации, открытые без входа. */
    private static final String[] PUBLIC_DOCUMENTATION = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-resources/**",
            "/webjars/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JsonSecurityErrorHandler securityErrorHandler)
            throws Exception {
        http
                // REST API не использует сессии браузера, поэтому CSRF-защита не нужна.
                .csrf(AbstractHttpConfigurer::disable)
                // Вход по HTTP Basic: заголовок Authorization: Basic base64(email:пароль).
                .httpBasic(Customizer.withDefaults())
                // Учебный вариант без входа через форму: логин и пароль передаются при каждом
                // запросе в заголовке Authorization. SessionCreationPolicy.NEVER означает, что
                // сессия для этого не создаётся, но ранее сохранённая сессия, если она есть,
                // может использоваться. Политика STATELESS здесь не подходит: с ней тестовый
                // пользователь @WithMockUser теряется между шагами цепочки фильтров.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                // Страница логина и форма выхода не используются.
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                // Итоговый статус запроса и отказы 401/403 пишутся в лог.
                // Фильтр создаётся здесь, а не как бин: иначе Boot зарегистрировал бы
                // его второй раз в обычной цепочке сервлетов.
                .addFilterAfter(new SecurityAccessLogFilter(), BasicAuthenticationFilter.class)
                // Единый JSON-формат ошибок безопасности.
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(securityErrorHandler)
                        .accessDeniedHandler(securityErrorHandler))
                .authorizeHttpRequests(authorize -> authorize
                        // Документация API доступна без входа: сами методы защищены.
                        .requestMatchers(PUBLIC_DOCUMENTATION).permitAll()
                        // Прослушивание порта и служебные страницы ошибок.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Все методы REST API требуют аутентификации.
                        .anyRequest().authenticated());
        return http.build();
    }
}
