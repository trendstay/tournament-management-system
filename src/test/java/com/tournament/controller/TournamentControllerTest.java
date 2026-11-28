package com.tournament.controller;
import com.tournament.dto.tournament.TournamentRequest;
import com.tournament.exception.AccessDeniedException;
import com.tournament.dto.tournament.TournamentResponse;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Тесты REST-контроллера турниров. Сервис подменён Mockito-моком.
 * Запросы выполняются от имени тестового администратора (см. AbstractSecuredMockMvcSupport).
 */
@DisplayName("TournamentController: REST API турниров")
class TournamentControllerTest extends AbstractSecuredMockMvcSupport {
    @Autowired
    private MockMvc mockMvc;
    private TournamentResponse sampleTournament() {
        TournamentResponse response = new TournamentResponse();
        response.setId(100L);
        response.setTitle("Осенний Кубок");
        response.setSportType("FOOTBALL");
        response.setSportDisplayName("Футбол");
        response.setTeamsCount(2);
        response.setStandingsTable("=== ТУРНИРНАЯ ТАБЛИЦА ===\n");
        return response;
    }
    @Test
    @DisplayName("POST /api/tournaments возвращает 201")
    void create_shouldReturnCreatedTournament() throws Exception {
        when(tournamentService.create(any(TournamentRequest.class))).thenReturn(sampleTournament());
        mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Осенний Кубок\",\"sportType\":\"FOOTBALL\","
                                + "\"teamIds\":[1,2]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sportDisplayName").value("Футбол"));
    }
    @Test
    @DisplayName("POST /api/tournaments без вида спорта возвращает 400")
    void create_withoutSportType_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Кубок\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    @DisplayName("GET /api/tournaments/search фильтрует по виду спорта")
    void search_bySportType_shouldReturnFilteredTournaments() throws Exception {
        when(tournamentService.findByFilter(any())).thenReturn(List.of(sampleTournament()));
        mockMvc.perform(get("/api/tournaments/search").param("sportType", "FOOTBALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Осенний Кубок"));
    }
    @Test
    @DisplayName("GET /api/tournaments/{id}/standings возвращает турнирную таблицу")
    void standings_shouldReturnTable() throws Exception {
        when(tournamentService.getStandingsTable(100L)).thenReturn("=== ТУРНИРНАЯ ТАБЛИЦА ===");
        mockMvc.perform(get("/api/tournaments/100/standings"))
                .andExpect(status().isOk())
                .andExpect(content().string("=== ТУРНИРНАЯ ТАБЛИЦА ==="));
    }
    @Test
    @DisplayName("POST /api/tournaments/{id}/teams регистрирует команду")
    void registerTeam_shouldReturnTournament() throws Exception {
        when(tournamentService.registerTeam(eq(100L), eq(1L))).thenReturn(sampleTournament());
        mockMvc.perform(post("/api/tournaments/100/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"teamId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamsCount").value(2));
    }
    @Test
    @DisplayName("GET /api/tournaments/{id} возвращает турнир")
    void findById_shouldReturnTournament() throws Exception {
        when(tournamentService.findById(100L)).thenReturn(sampleTournament());
        mockMvc.perform(get("/api/tournaments/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }
    @Test
    @DisplayName("POST /api/tournaments: операция запрещена и возвращает 403")
    void create_withForbiddenReferee_shouldReturnForbiddenResponse() throws Exception {
        when(tournamentService.create(any(TournamentRequest.class))).thenThrow(
                new AccessDeniedException("назначение судьёй турнира",
                        "пользователь с ID 1 не является судьёй",
                        "Выберите пользователя с ролью REFEREE"));

        mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Кубок\",\"sportType\":\"FOOTBALL\","
                                + "\"refereeId\":1,\"teamIds\":[]}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value(
                        "Операция «назначение судьёй турнира» недоступна: пользователь с ID 1 не является судьёй"));
    }
}
