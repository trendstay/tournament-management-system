package com.tournament.controller;
import com.tournament.dto.match.BatchMatchRequest;
import com.tournament.dto.match.FinishMatchRequest;
import com.tournament.dto.match.MatchFilter;
import com.tournament.dto.match.MatchRequest;
import com.tournament.dto.match.MatchResponse;
import com.tournament.service.MatchService;
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
import java.time.LocalDateTime;
import java.util.List;
/**
 * REST-контроллер матчей (слой Controller модели MVC).
 */
@RestController
@RequestMapping("/api/matches")
public class MatchController {
    private final MatchService matchService;
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }
    /** CREATE: POST /api/matches */
    @PostMapping
    public ResponseEntity<MatchResponse> create(@Valid @RequestBody MatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.create(request));
    }
    /** READ: GET /api/matches/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.findById(id));
    }
    /** READ: GET /api/matches */
    @GetMapping
    public ResponseEntity<List<MatchResponse>> findAll() {
        return ResponseEntity.ok(matchService.findAll());
    }
    /** UPDATE: PUT /api/matches/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<MatchResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody MatchRequest request) {
        return ResponseEntity.ok(matchService.update(id, request));
    }
    /** DELETE: DELETE /api/matches/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matchService.delete(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * READ с фильтром: GET /api/matches/search?status=FINISHED
     * или ?tournamentId=1, или ?teamId=2, или ?from=2026-01-01T00:00:00&amp;to=2026-12-31T23:59:59
     */
    @GetMapping("/search")
    public ResponseEntity<List<MatchResponse>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long tournamentId,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
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
    public ResponseEntity<MatchResponse> finishMatch(@PathVariable Long id,
                                                     @Valid @RequestBody FinishMatchRequest request) {
        return ResponseEntity.ok(matchService.finishMatch(id, request));
    }
    /** Пакетная многопоточная обработка: POST /api/matches/process */
    @PostMapping("/process")
    public ResponseEntity<List<MatchResponse>> process(@Valid @RequestBody BatchMatchRequest request) {
        return ResponseEntity.ok(matchService.processMatchesConcurrently(request));
    }
}
