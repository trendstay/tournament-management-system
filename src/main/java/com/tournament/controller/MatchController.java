package com.tournament.controller;

import com.tournament.dto.match.BatchMatchRequest;
import com.tournament.dto.match.FinishMatchRequest;
import com.tournament.dto.match.MatchFilter;
import com.tournament.dto.match.MatchRequest;
import com.tournament.dto.match.MatchResponse;
import com.tournament.service.MatchService;
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

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST-контроллер матчей (слой Controller модели MVC).
 *
 * <p>Лабораторная работа №4: расписание матчей создают и изменяют ADMIN и MANAGER,
 * завершает матчи REFEREE (и ADMIN). Пакетная обработка матчей доступна только ADMIN,
 * удаление матча — только ADMIN. Просмотр матчей — любому аутентифицированному пользователю.</p>
 */
@RestController
@RequestMapping("/api/matches")
@Tag(name = "Матчи", description = "Расписание матчей, завершение матча и пакетная обработка результатов")
@SecurityRequirement(name = "basicAuth")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    /** CREATE: POST /api/matches */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Создать матч",
            description = "Команды должны различаться; матч может быть привязан к турниру, счёт задаётся в формате X:Y.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Матч создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или некорректный JSON"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или MANAGER"),
            @ApiResponse(responseCode = "404", description = "Команда или турнир не найдены"),
            @ApiResponse(responseCode = "422", description = "Команда не может играть сама с собой или неверный счёт"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<MatchResponse> create(@Valid @RequestBody MatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.create(request));
    }

    /** READ: GET /api/matches/{id} */
    @GetMapping("/{id}")
    @Operation(summary = "Получить матч по идентификатору",
            description = "Доступно любому аутентифицированному пользователю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Матч найден"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "404", description = "Матч с таким ID не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<MatchResponse> findById(
            @Parameter(description = "Идентификатор матча", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(matchService.findById(id));
    }

    /** READ: GET /api/matches */
    @GetMapping
    @Operation(summary = "Получить список матчей",
            description = "Доступно любому аутентифицированному пользователю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список матчей"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<MatchResponse>> findAll() {
        return ResponseEntity.ok(matchService.findAll());
    }

    /** UPDATE: PUT /api/matches/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Изменить матч",
            description = "Меняются команды, дата, турнир и счёт матча.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Матч изменён"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или некорректный JSON"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или MANAGER"),
            @ApiResponse(responseCode = "404", description = "Матч, команда или турнир не найдены"),
            @ApiResponse(responseCode = "422", description = "Команда не может играть сама с собой или неверный счёт"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<MatchResponse> update(
            @Parameter(description = "Идентификатор матча", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody MatchRequest request) {
        return ResponseEntity.ok(matchService.update(id, request));
    }

    /** DELETE: DELETE /api/matches/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить матч",
            description = "Удаление доступно только ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Матч удалён"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN"),
            @ApiResponse(responseCode = "404", description = "Матч с таким ID не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Идентификатор матча", example = "1")
            @PathVariable Long id) {
        matchService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * READ с фильтром: GET /api/matches/search?status=FINISHED
     * или ?tournamentId=1, или ?teamId=2, или ?from=2026-01-01T00:00:00&amp;to=2026-12-31T23:59:59
     */
    @GetMapping("/search")
    @Operation(summary = "Найти матчи по фильтру",
            description = "Используется первый заполненный фильтр: статус, турнир, команда или период даты матча.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найденные матчи"),
            @ApiResponse(responseCode = "400", description = "Неизвестный статус, неверный формат даты или задана одна граница периода"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<MatchResponse>> search(
            @Parameter(description = "Статус матча: SCHEDULED, IN_PROGRESS, FINISHED или CANCELLED", example = "FINISHED")
            @RequestParam(required = false) String status,
            @Parameter(description = "Идентификатор турнира", example = "1")
            @RequestParam(required = false) Long tournamentId,
            @Parameter(description = "Идентификатор команды", example = "2")
            @RequestParam(required = false) Long teamId,
            @Parameter(description = "Начало периода, формат ISO: 2026-01-01T00:00:00", example = "2026-01-01T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Конец периода, формат ISO: 2026-12-31T23:59:59", example = "2026-12-31T23:59:59")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        MatchFilter filter = new MatchFilter();
        filter.setStatus(status);
        filter.setTournamentId(tournamentId);
        filter.setTeamId(teamId);
        filter.setFrom(from);
        filter.setTo(to);
        return ResponseEntity.ok(matchService.findByFilter(filter));
    }

    /** Завершение матча: PUT /api/matches/{id}/finish */
    @PutMapping("/{id}/finish")
    @PreAuthorize("hasAnyRole('ADMIN', 'REFEREE')")
    @Operation(summary = "Завершить матч",
            description = "Судья вносит итоговый счёт; ничья допустима не во всех видах спорта.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Матч завершён"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или некорректный JSON"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или REFEREE"),
            @ApiResponse(responseCode = "404", description = "Матч с таким ID не найден"),
            @ApiResponse(responseCode = "422", description = "Ничья недопустима в дисциплине турнира"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<MatchResponse> finishMatch(
            @Parameter(description = "Идентификатор матча", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody FinishMatchRequest request) {
        return ResponseEntity.ok(matchService.finishMatch(id, request));
    }

    /** Пакетная многопоточная обработка: POST /api/matches/process */
    @PostMapping("/process")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Пакетная обработка матчей",
            description = "Расчёт результатов пачкой в пуле потоков; операция доступна только ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Обработанные матчи"),
            @ApiResponse(responseCode = "400", description = "Пустой список матчей или некорректный JSON"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN"),
            @ApiResponse(responseCode = "404", description = "Один из матчей не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<MatchResponse>> process(@Valid @RequestBody BatchMatchRequest request) {
        return ResponseEntity.ok(matchService.processMatchesConcurrently(request));
    }
}
