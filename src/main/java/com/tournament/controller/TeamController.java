package com.tournament.controller;
import com.tournament.dto.team.CaptainRequest;
import com.tournament.dto.team.TeamFilter;
import com.tournament.dto.team.TeamRequest;
import com.tournament.dto.team.TeamResponse;
import com.tournament.dto.user.UserResponse;
import com.tournament.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 */
@RestController
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamService teamService;
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }
    /** CREATE: POST /api/teams */
    @PostMapping
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody TeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.create(request));
    }
    /** READ: GET /api/teams/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.findById(id));
    }
    /** READ: GET /api/teams */
    @GetMapping
    public ResponseEntity<List<TeamResponse>> findAll() {
        return ResponseEntity.ok(teamService.findAll());
    }
    /** UPDATE: PUT /api/teams/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody TeamRequest request) {
        return ResponseEntity.ok(teamService.update(id, request));
    }
    /** DELETE: DELETE /api/teams/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teamService.delete(id);
        return ResponseEntity.noContent().build();
    }
    /** READ с фильтром: GET /api/teams/search?city=Москва&amp;name=Спартак */
    @GetMapping("/search")
    public ResponseEntity<List<TeamResponse>> search(@RequestParam(required = false) String city,
                                                     @RequestParam(required = false) String name,
                                                     @RequestParam(required = false) Long coachId) {
        TeamFilter filter = new TeamFilter();
        filter.setCity(city);
        filter.setName(name);
        filter.setCoachId(coachId);
        return ResponseEntity.ok(teamService.findByFilter(filter));
    }
    /** Состав команды: GET /api/teams/{id}/players */
    @GetMapping("/{id}/players")
    public ResponseEntity<List<UserResponse>> findPlayers(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.findPlayers(id));
    }
    /** Добавить игрока: POST /api/teams/{id}/players/{playerId} */
    @PostMapping("/{id}/players/{playerId}")
    public ResponseEntity<TeamResponse> addPlayer(@PathVariable Long id, @PathVariable Long playerId) {
        return ResponseEntity.ok(teamService.addPlayer(id, playerId));
    }
    /** Удалить игрока: DELETE /api/teams/{id}/players/{playerId} */
    @DeleteMapping("/{id}/players/{playerId}")
    public ResponseEntity<TeamResponse> removePlayer(@PathVariable Long id, @PathVariable Long playerId) {
        return ResponseEntity.ok(teamService.removePlayer(id, playerId));
    }
    /** Назначить капитана: PUT /api/teams/{id}/captain */
    @PutMapping("/{id}/captain")
    public ResponseEntity<TeamResponse> appointCaptain(@PathVariable Long id,
                                                       @Valid @RequestBody CaptainRequest request) {
        return ResponseEntity.ok(teamService.appointCaptain(id, request.getPlayerId()));
    }
}
