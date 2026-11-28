package com.tournament.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Фильтр журналирования доступов к REST API.
 *
 * <p>Пишет в лог три события, которые удобно показать на защите лабораторной работы:</p>
 * <ul>
 *   <li>вход в защищённый метод — с указанием пользователя и его роли;</li>
 *   <li>отказ по роли (HTTP 403) — такой запрос уже прошёл аутентификацию,
 *       поэтому фильтр видит имя пользователя;</li>
 *   <li>запрос без учётных данных (HTTP 401).</li>
 * </ul>
 *
 * <p>Пароль и заголовок {@code Authorization} в лог не попадают: выводятся только
 * адрес запроса, email пользователя и его роль.</p>
 *
 * <p>Фильтр ставится сразу после {@code BasicAuthenticationFilter}: к этому моменту
 * аутентификация по HTTP Basic уже выполнена, а решение о правах — ещё нет,
 * поэтому код ответа 403 ещё можно увидеть.</p>
 *
 * <p>Класс намеренно не помечен {@code @Component}: Spring Boot автоматически
 * регистрирует любой бин-фильтр ещё и в обычной цепочке сервлетов, и тогда каждое
 * событие писалось бы в лог дважды. Фильтр создаётся только в {@code SecurityConfig}.</p>
 */
public class SecurityAccessLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityAccessLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            int status = response.getStatus();
            if (status == HttpServletResponse.SC_UNAUTHORIZED) {
                log.warn("Отказ в доступе: запрос {} {} выполнен без аутентификации (401)",
                        request.getMethod(), request.getRequestURI());
            } else if (status == HttpServletResponse.SC_FORBIDDEN) {
                log.warn("Отказ в доступе: пользователь {} с ролью {} не имеет прав на {} {} (403)",
                        username(), role(), request.getMethod(), request.getRequestURI());
            } else {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    log.info("Запрос {} {} выполнен пользователем {} с ролью {}",
                            request.getMethod(), request.getRequestURI(), username(), role());
                }
            }
        }
    }

    /** Email текущего пользователя (имя входа) или {@code anonymous} при анонимном доступе. */
    private String username() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }
        return authentication.getName();
    }

    /** Роль текущего пользователя в коротком виде, например {@code ADMIN}. */
    private String role() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "нет";
        }
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String value = authority.getAuthority();
            roles.add(value.startsWith("ROLE_") ? value.substring("ROLE_".length()) : value);
        }
        return roles.isEmpty() ? "нет" : String.join(",", roles);
    }
}
