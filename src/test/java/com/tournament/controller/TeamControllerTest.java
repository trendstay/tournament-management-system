package com.tournament.controller;
import com.tournament.dto.team.TeamResponse;
import com.tournament.exception.ConflictException;
import com.tournament.support.AbstractSecuredMockMvcSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Тесты REST-контроллера команд. Сервис подменён Mockito-моком.
 * Запросы выполняются от имени тестового администратора (см. AbstractSecuredMockMvcSupport).
 */
@DisplayName("TeamController: REST API команд")
class TeamControllerTest extends AbstractSecuredMockMvcSupport {
    @Autowired
    private MockMvc mockMvc;
    private TeamResponse sampleTeam() {
        TeamResponse response = new TeamResponse();
        response.setId(10L);
        response.setName("Зенит");
        response.setCity("Санкт-Петербург");
        response.setPlayersCount(1);
        return response;
    }
    @Test
    @DisplayName("POST /api/teams возвращает 201")
    void create_shouldReturnCreatedTeam() throws Exception {
        when(teamService.create(any())).thenReturn(sampleTeam());
        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Зенит\",\"city\":\"Санкт-Петербург\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Зенит"));
    }
    @Test
    @DisplayName("POST /api/teams без названия возвращает 400")
    void create_withoutName_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"city\":\"Санкт-Петербург\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    @DisplayName("GET /api/teams возвращает список команд")
    void findAll_shouldReturnTeams() throws Exception {
        when(teamService.findAll()).thenReturn(List.of(sampleTeam()));
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Санкт-Петербург"));
    }
    @Test
    @DisplayName("GET /api/teams/search фильтрует по городу")
    void search_byCity_shouldReturnFilteredTeams() throws Exception {
        when(teamService.findByFilter(any())).thenReturn(List.of(sampleTeam()));
        mockMvc.perform(get("/api/teams/search").param("city", "Санкт-Петербург"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    @Test
    @DisplayName("POST /api/teams/{id}/players/{playerId} добавляет игрока")
    void addPlayer_shouldReturnTeam() throws Exception {
        when(teamService.addPlayer(10L, 3L)).thenReturn(sampleTeam());
        mockMvc.perform(post("/api/teams/10/players/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playersCount").value(1));
    }
    @Test
    @DisplayName("DELETE /api/teams/{id} возвращает 204")
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/teams/10"))
                .andExpect(status().isNoContent());
        verify(teamService).delete(10L);
    }
    @Test
    @DisplayName("POST /api/teams: конфликт названия возвращает 409")
    void create_withDuplicateName_shouldReturnConflictResponse() throws Exception {
        when(teamService.create(any())).thenThrow(new ConflictException(
                "Команда с названием «Зенит» уже существует",
                "Укажите другое название команды", "TEAM_NAME_EXISTS"));

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Зенит\",\"city\":\"Москва\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("TEAM_NAME_EXISTS"))
                .andExpect(jsonPath("$.suggestion").isNotEmpty());
    }
}
