package com.tournament.controller;
import com.tournament.dto.tournament.RegisterTeamRequest;
import com.tournament.dto.tournament.TournamentFilter;
import com.tournament.dto.tournament.TournamentRequest;
import com.tournament.dto.tournament.TournamentResponse;
import com.tournament.model.enums.SportType;
import com.tournament.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.time.LocalDate;
import java.util.List;
/**
 * REST-контроллер турниров (слой Controller модели MVC).
 */
@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {
    private final TournamentService tournamentService;
    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }
    /** CREATE: POST /api/tournaments */
    @PostMapping
    public ResponseEntity<TournamentResponse> create(@Valid @RequestBody TournamentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.create(request));
    }
    /** READ: GET /api/tournaments/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.findById(id));
    }
    /** READ: GET /api/tournaments */
    @GetMapping
    public ResponseEntity<List<TournamentResponse>> findAll() {
        return ResponseEntity.ok(tournamentService.findAll());
    }
    /** UPDATE: PUT /api/tournaments/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<TournamentResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody TournamentRequest request) {
        return ResponseEntity.ok(tournamentService.update(id, request));
    }
    /** DELETE: DELETE /api/tournaments/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tournamentService.delete(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * READ с фильтром: GET /api/tournaments/search?sportType=FOOTBALL
     * или ?title=Кубок, или ?startFrom=2026-01-01&amp;startTo=2026-12-31, или ?refereeId=1
     */
    @GetMapping("/search")
    public ResponseEntity<List<TournamentResponse>> search(
            @RequestParam(required = false) SportType sportType,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startTo,
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
    public ResponseEntity<TournamentResponse> registerTeam(@PathVariable Long id,
                                                           @Valid @RequestBody RegisterTeamRequest request) {
        return ResponseEntity.ok(tournamentService.registerTeam(id, request.getTeamId()));
    }
    /** Исключение команды: DELETE /api/tournaments/{id}/teams/{teamId} */
    @DeleteMapping("/{id}/teams/{teamId}")
    public ResponseEntity<TournamentResponse> unregisterTeam(@PathVariable Long id,
                                                             @PathVariable Long teamId) {
        return ResponseEntity.ok(tournamentService.unregisterTeam(id, teamId));
    }
    /** Турнирная таблица: GET /api/tournaments/{id}/standings */
    @GetMapping("/{id}/standings")
    public ResponseEntity<String> standings(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.getStandingsTable(id));
    }
}
