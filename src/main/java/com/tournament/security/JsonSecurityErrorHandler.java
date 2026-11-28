package com.tournament.security;

import com.tournament.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Единый JSON-ответ на отказ Spring Security.
 *
 * <p>Класс реализует сразу два интерфейса Spring Security:</p>
 * <ul>
 *   <li>{@link AuthenticationEntryPoint} — запрос без учётных данных или с неверным
 *       паролем: ответ 401;</li>
 *   <li>{@link AccessDeniedHandler} — пользователь вошёл, но роли не хватает: ответ 403.</li>
 * </ul>
 *
 * <p>Ответ собирается в тот же формат {@link ErrorResponse}, что и ошибки бизнес-логики
 * из лабораторной работы №3, поэтому клиент всегда получает одинаковую структуру:
 * {@code timestamp, status, errorCode, message, suggestion, details}.</p>
 *
 * <p>Важно: используется именно {@code org.springframework.security.access.AccessDeniedException},
 * а не собственный {@code com.tournament.exception.AccessDeniedException} — это разные
 * исключения с разными сценариями.</p>
 */
@Component
public class JsonSecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JsonSecurityErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** 401: запрос не аутентифицирован. */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        write(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED",
                "Для выполнения запроса необходима аутентификация",
                "Передайте email и пароль в заголовке Authorization с типом Basic");
    }

    /** 403: пользователь аутентифицирован, но у его роли нет прав на операцию. */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        write(response, HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                "Недостаточно прав для выполнения операции",
                "Обратитесь к администратору: операция доступна только пользователям с подходящей ролью");
    }

    private void write(HttpServletResponse response,
                       HttpStatus status,
                       String errorCode,
                       String message,
                       String suggestion) throws IOException {
        ErrorResponse body = new ErrorResponse(status.value(), errorCode, message, suggestion);
        response.setStatus(status.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
