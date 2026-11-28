package com.tournament.controller;

import com.tournament.dto.tournament.RegisterTeamRequest;
import com.tournament.dto.tournament.TournamentFilter;
import com.tournament.dto.tournament.TournamentRequest;
import com.tournament.dto.tournament.TournamentResponse;
import com.tournament.model.enums.SportType;
import com.tournament.service.TournamentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

/**
 * REST-контроллер турниров (слой Controller модели MVC).
 *
 * <p>Лабораторная работа №4: создание, изменение, регистрация команд в турнире
 * и исключение команды доступны ADMIN и MANAGER; удаление турнира — только ADMIN.
 * Просмотр турниров и турнирной таблицы — любому аутентифицированному пользователю.</p>
 */
@RestController
@RequestMapping("/api/tournaments")
@Tag(name = "Турниры", description = "Турниры, регистрация команд и турнирная таблица")
@SecurityRequirement(name = "basicAuth")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    /** CREATE: POST /api/tournaments */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Создать турнир",
            description = "Дата окончания не может быть раньше даты начала; судья указывается по идентификатору.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Турнир создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или некорректный JSON"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или MANAGER"),
            @ApiResponse(responseCode = "404", description = "Судья или команда не найдены"),
            @ApiResponse(responseCode = "422", description = "Нарушено правило дат турнира"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TournamentResponse> create(@Valid @RequestBody TournamentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.create(request));
    }

    /** READ: GET /api/tournaments/{id} */
    @GetMapping("/{id}")
    @Operation(summary = "Получить турнир по идентификатору",
            description = "Доступно любому аутентифицированному пользователю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Турнир найден"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "404", description = "Турнир с таким ID не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TournamentResponse> findById(
            @Parameter(description = "Идентификатор турнира", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.findById(id));
    }

    /** READ: GET /api/tournaments */
    @GetMapping
    @Operation(summary = "Получить список турниров",
            description = "Доступно любому аутентифицированному пользователю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список турниров"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<TournamentResponse>> findAll() {
        return ResponseEntity.ok(tournamentService.findAll());
    }

    /** UPDATE: PUT /api/tournaments/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Изменить турнир",
            description = "Меняются название, вид спорта, даты, призовой фонд, судья и команды-участницы.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Турнир изменён"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или некорректный JSON"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или MANAGER"),
            @ApiResponse(responseCode = "404", description = "Турнир, судья или команда не найдены"),
            @ApiResponse(responseCode = "422", description = "Нарушено правило дат турнира"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TournamentResponse> update(
            @Parameter(description = "Идентификатор турнира", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TournamentRequest request) {
        return ResponseEntity.ok(tournamentService.update(id, request));
    }

    /** DELETE: DELETE /api/tournaments/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить турнир",
            description = "Удаление доступно только ADMIN; турнир не должен иметь привязанных матчей.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Турнир удалён"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN"),
            @ApiResponse(responseCode = "404", description = "Турнир с таким ID не найден"),
            @ApiResponse(responseCode = "422", description = "К турниру привязаны матчи"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Идентификатор турнира", example = "1")
            @PathVariable Long id) {
        tournamentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * READ с фильтром: GET /api/tournaments/search?sportType=FOOTBALL
     * или ?title=Кубок, или ?startFrom=2026-01-01&amp;startTo=2026-12-31, или ?refereeId=1
     */
    @GetMapping("/search")
    @Operation(summary = "Найти турниры по фильтру",
            description = "Используется первый заполненный фильтр: вид спорта, название, период дат или судья.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найденные турниры"),
            @ApiResponse(responseCode = "400", description = "Задана только одна граница дат или неверный формат даты"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<TournamentResponse>> search(
            @Parameter(description = "Вид спорта", example = "FOOTBALL")
            @RequestParam(required = false) SportType sportType,
            @Parameter(description = "Часть названия турнира", example = "Кубок")
            @RequestParam(required = false) String title,
            @Parameter(description = "Начало периода (включительно), формат yyyy-MM-dd", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startFrom,
            @Parameter(description = "Конец периода (включительно), формат yyyy-MM-dd", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startTo,
            @Parameter(description = "Идентификатор судьи", example = "3")
            @RequestParam(required = false) Long refereeId) {
        TournamentFilter filter = new TournamentFilter();
        filter.setSportType(sportType);
        filter.setTitle(title);
        filter.setStartFrom(startFrom);
        filter.setStartTo(startTo);
        filter.setRefereeId(refereeId);
        return ResponseEntity.ok(tournamentService.findByFilter(filter));
    }

    /** Регистрация команды: POST /api/tournaments/{id}/teams */
    @PostMapping("/{id}/teams")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Зарегистрировать команду в турнире",
            description = "Команда добавляется в список участников; повторная регистрация запрещена.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Команда зарегистрирована, возвращён турнир"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или некорректный JSON"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или MANAGER"),
            @ApiResponse(responseCode = "404", description = "Турнир или команда не найдены"),
            @ApiResponse(responseCode = "409", description = "Команда уже участвует в турнире"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TournamentResponse> registerTeam(
            @Parameter(description = "Идентификатор турнира", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RegisterTeamRequest request) {
        return ResponseEntity.ok(tournamentService.registerTeam(id, request.getTeamId()));
    }

    /** Исключение команды: DELETE /api/tournaments/{id}/teams/{teamId} */
    @DeleteMapping("/{id}/teams/{teamId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Исключить команду из турнира",
            description = "Команда удаляется из списка участников; исключить можно только зарегистрированную команду.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Команда исключена, возвращён турнир"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или MANAGER"),
            @ApiResponse(responseCode = "404", description = "Турнир с таким ID не найден"),
            @ApiResponse(responseCode = "409", description = "Команда не участвует в турнире"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TournamentResponse> unregisterTeam(
            @Parameter(description = "Идентификатор турнира", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Идентификатор команды", example = "2")
            @PathVariable Long teamId) {
        return ResponseEntity.ok(tournamentService.unregisterTeam(id, teamId));
    }

    /** Турнирная таблица: GET /api/tournaments/{id}/standings */
    @GetMapping("/{id}/standings")
    @Operation(summary = "Получить турнирную таблицу",
            description = "Таблица строится по завершённым матчам: победы, ничьи, поражения и очки (3/1/0).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Текстовая турнирная таблица"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "404", description = "Турнир с таким ID не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<String> standings(
            @Parameter(description = "Идентификатор турнира", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.getStandingsTable(id));
    }
}
