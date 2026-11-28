package com.tournament.controller;
import com.tournament.dto.match.MatchResponse;
import com.tournament.exception.BusinessRuleException;
import com.tournament.support.AbstractSecuredMockMvcSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Тесты REST-контроллера матчей. Сервис подменён Mockito-моком.
 * Запросы выполняются от имени тестового администратора (см. AbstractSecuredMockMvcSupport).
 */
@DisplayName("MatchController: REST API матчей")
class MatchControllerTest extends AbstractSecuredMockMvcSupport {
    @Autowired
    private MockMvc mockMvc;
    private MatchResponse sampleMatch() {
        MatchResponse response = new MatchResponse();
        response.setId(50L);
        response.setHomeTeamName("Зенит");
        response.setAwayTeamName("Спартак");
        response.setScore("3:1");
        response.setStatus("FINISHED");
        response.setWinnerTeamName("Зенит");
        return response;
    }
    @Test
    @DisplayName("POST /api/matches возвращает 201")
    void create_shouldReturnCreatedMatch() throws Exception {
        when(matchService.create(any())).thenReturn(sampleMatch());
        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"homeTeamId\":1,\"awayTeamId\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.homeTeamName").value("Зенит"));
    }
    @Test
    @DisplayName("POST /api/matches без команд возвращает 400")
    void create_withoutTeams_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    @DisplayName("PUT /api/matches/{id}/finish завершает матч")
    void finishMatch_shouldReturnFinishedMatch() throws Exception {
        when(matchService.finishMatch(eq(50L), any())).thenReturn(sampleMatch());
        mockMvc.perform(put("/api/matches/50/finish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":\"3:1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINISHED"))
                .andExpect(jsonPath("$.winnerTeamName").value("Зенит"));
    }
    @Test
    @DisplayName("GET /api/matches/search фильтрует по статусу")
    void search_byStatus_shouldReturnFilteredMatches() throws Exception {
        when(matchService.findByFilter(any())).thenReturn(List.of(sampleMatch()));
        mockMvc.perform(get("/api/matches/search").param("status", "FINISHED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    @Test
    @DisplayName("POST /api/matches/process запускает пакетную обработку")
    void process_shouldReturnProcessedMatches() throws Exception {
        when(matchService.processMatchesConcurrently(any())).thenReturn(List.of(sampleMatch()));
        mockMvc.perform(post("/api/matches/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matchIds\":[50]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(50));
    }
    @Test
    @DisplayName("GET /api/matches/{id} возвращает матч")
    void findById_shouldReturnMatch() throws Exception {
        when(matchService.findById(50L)).thenReturn(sampleMatch());
        mockMvc.perform(get("/api/matches/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value("3:1"));
    }
    @Test
    @DisplayName("PUT /api/matches/{id}/finish: запрещённая ничья возвращает 422")
    void finishMatch_withDisallowedDraw_shouldReturnBusinessRuleResponse() throws Exception {
        when(matchService.finishMatch(eq(50L), any())).thenThrow(
                new BusinessRuleException("Ничья недопустима в дисциплине Баскетбол",
                        "Измените счёт или назначьте дополнительное время", "DRAW_NOT_ALLOWED"));

        mockMvc.perform(put("/api/matches/50/finish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":\"1:1\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value("DRAW_NOT_ALLOWED"))
                .andExpect(jsonPath("$.suggestion").isNotEmpty());
    }
}
