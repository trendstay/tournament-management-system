package com.tournament.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/** Отдельно преобразует каждый известный тип ошибки в понятный REST-ответ. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException exception) {
        return build(HttpStatus.CONFLICT, exception);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException exception) {
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getErrorCode(),
                exception.getMessage(), exception.getSuggestion());
        body.setDetails(Map.of("errors", exception.getValidationErrors()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException exception) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, exception);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException exception) {
        return build(HttpStatus.FORBIDDEN, exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBeanValidation(MethodArgumentNotValidException exception) {
        Map<String, Object> fields = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fields.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "ARGUMENT_VALIDATION_ERROR",
                "Входные данные не прошли проверку: исправьте ошибки в указанных полях",
                "Проверьте обязательные поля и формат значений по деталям ответа");
        body.setDetails(fields);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String requiredType = exception.getRequiredType() == null
                ? "неизвестен" : exception.getRequiredType().getSimpleName();
        Map<String, Object> details = new HashMap<>();
        details.put("parameter", exception.getName());
        details.put("value", exception.getValue());
        details.put("requiredType", requiredType);

        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "ARGUMENT_TYPE_MISMATCH",
                "Параметр «" + exception.getName() + "» имеет неверный тип",
                "Передайте значение типа " + requiredType);
        body.setDetails(details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableJson(HttpMessageNotReadableException exception) {
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "MALFORMED_JSON",
                "Тело запроса не удалось прочитать как JSON",
                "Проверьте синтаксис JSON, обязательные кавычки, запятые и типы значений");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrity(DataIntegrityViolationException exception) {
        ErrorResponse body = new ErrorResponse(HttpStatus.CONFLICT.value(), "DATA_INTEGRITY_VIOLATION",
                "Операция нарушает ограничения целостности данных в базе",
                "Проверьте уникальность значений и наличие связанных записей, затем повторите запрос");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        String reason = exception.getMessage() == null ? "причина не указана" : exception.getMessage();
        ErrorResponse body = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_ERROR",
                "Внутренняя ошибка сервера: " + reason,
                "Повторите запрос позже; если ошибка повторяется, сообщите администратору");
        body.setDetails(Map.of("exception", exception.getClass().getSimpleName()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, TournamentException exception) {
        ErrorResponse body = new ErrorResponse(status.value(), exception.getErrorCode(),
                exception.getMessage(), exception.getSuggestion());
        return ResponseEntity.status(status).body(body);
    }
}
