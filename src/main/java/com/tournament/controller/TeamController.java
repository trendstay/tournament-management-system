package com.tournament.controller;

import com.tournament.dto.team.CaptainRequest;
import com.tournament.dto.team.TeamFilter;
import com.tournament.dto.team.TeamRequest;
import com.tournament.dto.team.TeamResponse;
import com.tournament.dto.user.UserResponse;
import com.tournament.service.TeamService;
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
 * REST-контроллер команд (слой Controller модели MVC).
 *
 * <p>Лабораторная работа №4: составом команды управляет тренер (COACH), а также
 * администратор; назначение капитана доступно ещё и капитану. Создание, изменение
 * и удаление команды — ADMIN и MANAGER.</p>
 */
@RestController
@RequestMapping("/api/teams")
@Tag(name = "Команды", description = "Команды, тренер, состав игроков и назначение капитана")
@SecurityRequirement(name = "basicAuth")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /** CREATE: POST /api/teams */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Создать команду",
            description = "Название команды должно быть уникальным; тренер указывается по идентификатору.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Команда создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или некорректный JSON"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или MANAGER"),
            @ApiResponse(responseCode = "404", description = "Тренер с таким ID не найден"),
            @ApiResponse(responseCode = "409", description = "Команда с таким названием уже существует"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody TeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.create(request));
    }

    /** READ: GET /api/teams/{id} */
    @GetMapping("/{id}")
    @Operation(summary = "Получить команду по идентификатору",
            description = "Доступно любому аутентифицированному пользователю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Команда найдена"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "404", description = "Команда с таким ID не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TeamResponse> findById(
            @Parameter(description = "Идентификатор команды", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(teamService.findById(id));
    }

    /** READ: GET /api/teams */
    @GetMapping
    @Operation(summary = "Получить список команд",
            description = "Доступно любому аутентифицированному пользователю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список команд"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<TeamResponse>> findAll() {
        return ResponseEntity.ok(teamService.findAll());
    }

    /** UPDATE: PUT /api/teams/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Изменить команду",
            description = "Меняются название, город и тренер команды.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Команда изменена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или некорректный JSON"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или MANAGER"),
            @ApiResponse(responseCode = "404", description = "Команда или тренер не найдены"),
            @ApiResponse(responseCode = "409", description = "Название уже занято другой командой"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TeamResponse> update(
            @Parameter(description = "Идентификатор команды", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TeamRequest request) {
        return ResponseEntity.ok(teamService.update(id, request));
    }

    /** DELETE: DELETE /api/teams/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить команду",
            description = "Удаление доступно только ADMIN; команда не должна иметь игроков и матчей.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Команда удалена"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN"),
            @ApiResponse(responseCode = "404", description = "Команда с таким ID не найдена"),
            @ApiResponse(responseCode = "409", description = "В команде есть игроки или у неё есть матчи"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Идентификатор команды", example = "1")
            @PathVariable Long id) {
        teamService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** READ с фильтром: GET /api/teams/search?city=Москва&amp;name=Спартак */
    @GetMapping("/search")
    @Operation(summary = "Найти команды по фильтру",
            description = "Фильтры необязательны; используется первый заполненный: city, name или coachId.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найденные команды"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<TeamResponse>> search(
            @Parameter(description = "Город команды", example = "Москва")
            @RequestParam(required = false) String city,
            @Parameter(description = "Часть названия команды", example = "Спартак")
            @RequestParam(required = false) String name,
            @Parameter(description = "Идентификатор тренера", example = "3")
            @RequestParam(required = false) Long coachId) {
        TeamFilter filter = new TeamFilter();
        filter.setCity(city);
        filter.setName(name);
        filter.setCoachId(coachId);
        return ResponseEntity.ok(teamService.findByFilter(filter));
    }

    /** Состав команды: GET /api/teams/{id}/players */
    @GetMapping("/{id}/players")
    @Operation(summary = "Получить состав команды",
            description = "Список игроков команды; доступен любому аутентифицированному пользователю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список игроков"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "404", description = "Команда с таким ID не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<UserResponse>> findPlayers(
            @Parameter(description = "Идентификатор команды", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(teamService.findPlayers(id));
    }

    /** Добавить игрока: POST /api/teams/{id}/players/{playerId} */
    @PostMapping("/{id}/players/{playerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    @Operation(summary = "Добавить игрока в состав",
            description = "Игрок добавляется в команду; если он был в другой команде, прежняя связь снимается.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Игрок добавлен, возвращена команда"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или COACH"),
            @ApiResponse(responseCode = "404", description = "Команда или игрок не найдены"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TeamResponse> addPlayer(
            @Parameter(description = "Идентификатор команды", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Идентификатор игрока", example = "5")
            @PathVariable Long playerId) {
        return ResponseEntity.ok(teamService.addPlayer(id, playerId));
    }

    /** Удалить игрока: DELETE /api/teams/{id}/players/{playerId} */
    @DeleteMapping("/{id}/players/{playerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    @Operation(summary = "Удалить игрока из состава",
            description = "Удаление из состава доступно ADMIN и COACH.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Игрок удалён из состава, возвращена команда"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN или COACH"),
            @ApiResponse(responseCode = "404", description = "Команда или игрок не найдены"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TeamResponse> removePlayer(
            @Parameter(description = "Идентификатор команды", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Идентификатор игрока", example = "5")
            @PathVariable Long playerId) {
        return ResponseEntity.ok(teamService.removePlayer(id, playerId));
    }

    /** Назначить капитана: PUT /api/teams/{id}/captain */
    @PutMapping("/{id}/captain")
    @PreAuthorize("hasAnyRole('ADMIN', 'COACH', 'CAPTAIN')")
    @Operation(summary = "Назначить капитана команды",
            description = "Роль игрока переключается на CAPTAIN; доступно ADMIN, COACH и CAPTAIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Капитан назначен, возвращена команда"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или некорректный JSON"),
            @ApiResponse(responseCode = "401", description = "Не передан заголовок Authorization"),
            @ApiResponse(responseCode = "403", description = "Нужна роль ADMIN, COACH или CAPTAIN"),
            @ApiResponse(responseCode = "404", description = "Команда или игрок не найдены"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<TeamResponse> appointCaptain(
            @Parameter(description = "Идентификатор команды", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CaptainRequest request) {
        return ResponseEntity.ok(teamService.appointCaptain(id, request.getPlayerId()));
    }
}
