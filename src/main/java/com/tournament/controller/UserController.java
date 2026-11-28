package com.tournament.controller;

import com.tournament.dto.user.UserFilter;
import com.tournament.dto.user.UserRequest;
import com.tournament.dto.user.UserResponse;
import com.tournament.exception.ValidationException;
import com.tournament.model.enums.UserRole;
import com.tournament.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер пользователей (слой Controller модели MVC).
 * Содержит только маршрутизацию запросов; бизнес-логика находится в сервисе.
 *
 * <p>Лабораторная работа №4: права на операции объявлены над методами.
 * Создавать, изменять и удалять пользователей может только ADMIN.
 * Просмотр списка пользователей доступен ADMIN и MANAGER, а данные конкретного
 * пользователя — любому аутентифицированному пользователю.</p>
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Пользователи", description = "Учётные записи: администраторы, менеджеры, судьи, тренеры и игроки")
@SecurityRequirement(name = "basicAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** CREATE: POST /api/users */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать пользователя",
            description = "Создаёт учётную запись нужной роли. Пароль сохраняется в виде BCrypt-хеша.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или некорректный JSON"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse created = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** READ: GET /api/users/{id} */
    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по идентификатору",
            description = "Данные доступны любому аутентифицированному пользователю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<UserResponse> findById(
            @Parameter(description = "Идентификатор пользователя", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /** READ: GET /api/users */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Получить список пользователей",
            description = "Список учётных записей; доступен администратору и менеджеру.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или MANAGER"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    /** UPDATE: PUT /api/users/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Изменить пользователя",
            description = "Если поле password пустое или не передано, прежний пароль сохраняется.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь изменён"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или некорректный JSON"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN"),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден"),
            @ApiResponse(responseCode = "409", description = "Email уже занят другим пользователем"),
            @ApiResponse(responseCode = "422", description = "Смена роли не поддерживается"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<UserResponse> update(
            @Parameter(description = "Идентификатор пользователя", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    /** DELETE: DELETE /api/users/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить пользователя",
            description = "Удаление доступно только ADMIN. Нельзя удалить капитана или тренера команды.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или пользователь связан с командой"),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Идентификатор пользователя", example = "1")
            @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * READ с фильтром: GET /api/users/search?role=COACH&amp;lastName=Ив
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Найти пользователей по фильтру",
            description = "Фильтры необязательны: role — роль пользователя, lastName — часть фамилии.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найденные пользователи"),
            @ApiResponse(responseCode = "400", description = "Неизвестное значение роли"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или MANAGER"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<UserResponse>> search(
            @Parameter(description = "Роль: ADMIN, MANAGER, REFEREE, COACH, CAPTAIN или PLAYER", example = "COACH")
            @RequestParam(required = false) String role,
            @Parameter(description = "Часть фамилии", example = "Ив")
            @RequestParam(required = false) String lastName) {
        UserFilter filter = new UserFilter();
        filter.setLastName(lastName);
        if (role != null && !role.isBlank()) {
            filter.setRole(parseRole(role));
        }
        return ResponseEntity.ok(userService.findByFilter(filter));
    }

    /**
     * Разбор роли из строки запроса.
     * Неизвестное значение — ошибка валидации (400), а не 500.
     */
    private UserRole parseRole(String role) {
        try {
            return UserRole.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Неизвестная роль пользователя: " + role,
                    "Укажите одну из ролей: ADMIN, MANAGER, REFEREE, COACH, CAPTAIN, PLAYER");
        }
    }

    /** READ с фильтром по email: GET /api/users/email/{email} */
    @GetMapping("/email/{email}")
    @Operation(summary = "Найти пользователя по email",
            description = "Поиск без учёта регистра; доступен любому аутентифицированному пользователю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким email не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<UserResponse> findByEmail(
            @Parameter(description = "Email пользователя", example = "coach@tournament.com")
            @PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }
}
