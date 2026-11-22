package com.tournament.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void notFoundException_returns404WithSuggestion() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(
                new NotFoundException("Команда с ID 99 не найдена"));

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("NOT_FOUND", response.getBody().getErrorCode());
        assertNotNull(response.getBody().getSuggestion());
    }

    @Test
    void conflictException_returns409() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConflict(
                new ConflictException("Пользователь с email a@test.com уже существует",
                        "Укажите другой email", "USER_EMAIL_EXISTS"));

        assertEquals(409, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("USER_EMAIL_EXISTS", response.getBody().getErrorCode());
    }

    @Test
    void validationException_returns400AndValidationDetails() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidation(
                new ValidationException(List.of("email обязателен", "роль обязательна")));

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(List.of("email обязателен", "роль обязательна"),
                response.getBody().getDetails().get("errors"));
    }

    @Test
    void businessRuleException_returns422() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessRule(
                new BusinessRuleException("Ничья недопустима в баскетболе",
                        "Измените счёт или назначьте дополнительное время", "DRAW_NOT_ALLOWED"));

        assertEquals(422, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("DRAW_NOT_ALLOWED", response.getBody().getErrorCode());
    }

    @Test
    void accessDeniedException_returns403() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDenied(
                new AccessDeniedException("создание команды", "пользователь не является тренером"));

        assertEquals(403, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("ACCESS_DENIED", response.getBody().getErrorCode());
    }

    @Test
    void dataIntegrityViolation_returns409() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIntegrity(
                new DataIntegrityViolationException("нарушено ограничение базы данных"));

        assertEquals(409, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("DATA_INTEGRITY_VIOLATION", response.getBody().getErrorCode());
    }

    @Test
    void unexpectedException_returns500() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnexpected(
                new IllegalStateException("непредвиденная ошибка"));

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().getErrorCode());
        assertEquals("IllegalStateException", response.getBody().getDetails().get("exception"));
    }
}
