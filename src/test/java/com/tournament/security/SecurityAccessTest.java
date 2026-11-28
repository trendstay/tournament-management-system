package com.tournament.security;

import com.tournament.dto.match.BatchMatchRequest;
import com.tournament.dto.match.FinishMatchRequest;
import com.tournament.dto.match.MatchRequest;
import com.tournament.dto.match.MatchResponse;
import com.tournament.dto.team.TeamRequest;
import com.tournament.dto.team.TeamResponse;
import com.tournament.dto.tournament.TournamentRequest;
import com.tournament.dto.tournament.TournamentResponse;
import com.tournament.dto.user.UserRequest;
import com.tournament.dto.user.UserResponse;
import com.tournament.model.enums.UserRole;
import com.tournament.support.AbstractSecuredMockMvcSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты разграничения прав доступа (лабораторная работа №4).
 *
 * <p>Проверяются три сценария:</p>
 * <ul>
 *   <li>запрос без входа получает 401 и единый JSON-формат ошибки;</li>
 *   <li>пользователь с неподходящей ролью получает 403, а не 500;</li>
 *   <li>пользователь с подходящей ролью выполняет операцию успешно.</li>
 * </ul>
 *
 * <p>Матрица прав (согласована перед выполнением работы):</p>
 * <pre>
 * ADMIN   — полный доступ, удаление пользователей, команд, турниров и матчей, пакетная обработка;
 * MANAGER — создание и изменение пользователей-справочников, команд и турниров, расписание матчей;
 * REFEREE — просмотр данных и завершение матчей;
 * COACH   — добавление и удаление игроков состава, назначение капитана;
 * CAPTAIN — просмотр данных и назначение капитана;
 * PLAYER  — только просмотр данных.
 * </pre>
 */
@DisplayName("Spring Security: матрица прав доступа к REST API")
class SecurityAccessTest extends AbstractSecuredMockMvcSupport {

    private static final String USER_JSON = "{\"email\":\"coach@test.com\",\"password\":\"pass123\","
            + "\"firstName\":\"Сергей\",\"lastName\":\"Тренеров\",\"role\":\"COACH\"}";

    @Autowired
    private MockMvc mockMvc;

    private UserResponse sampleUser() {
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setEmail("coach@test.com");
        response.setRole(UserRole.COACH);
        return response;
    }

    private TeamResponse sampleTeam() {
        TeamResponse response = new TeamResponse();
        response.setId(1L);
        response.setName("Зенит");
        return response;
    }

    private TournamentResponse sampleTournament() {
        TournamentResponse response = new TournamentResponse();
        response.setId(1L);
        response.setTitle("Осенний Кубок");
        return response;
    }

    private MatchResponse sampleMatch() {
        MatchResponse response = new MatchResponse();
        response.setId(1L);
        response.setScore("3:1");
        return response;
    }

    /** Все операции, у которых есть ограничение по роли. */
    static Stream<Arguments> protectedOperations() {
        return Stream.of(
                Arguments.of("POST", "/api/users", USER_JSON, List.of("ADMIN")),
                Arguments.of("GET", "/api/users", null, List.of("ADMIN", "MANAGER")),
                Arguments.of("PUT", "/api/users/1", USER_JSON, List.of("ADMIN")),
                Arguments.of("DELETE", "/api/users/1", null, List.of("ADMIN")),
                Arguments.of("GET", "/api/users/search", null, List.of("ADMIN", "MANAGER")),

                Arguments.of("POST", "/api/teams", "{\"name\":\"Зенит\"}", List.of("ADMIN", "MANAGER")),
                Arguments.of("PUT", "/api/teams/1", "{\"name\":\"Зенит\"}", List.of("ADMIN", "MANAGER")),
                Arguments.of("DELETE", "/api/teams/1", null, List.of("ADMIN")),
                Arguments.of("POST", "/api/teams/1/players/2", null, List.of("ADMIN", "COACH")),
                Arguments.of("DELETE", "/api/teams/1/players/2", null, List.of("ADMIN", "COACH")),
                Arguments.of("PUT", "/api/teams/1/captain", "{\"playerId\":2}",
                        List.of("ADMIN", "COACH", "CAPTAIN")),

                Arguments.of("POST", "/api/tournaments", "{\"title\":\"Кубок\",\"sportType\":\"FOOTBALL\"}",
                        List.of("ADMIN", "MANAGER")),
                Arguments.of("PUT", "/api/tournaments/1", "{\"title\":\"Кубок\",\"sportType\":\"FOOTBALL\"}",
                        List.of("ADMIN", "MANAGER")),
                Arguments.of("DELETE", "/api/tournaments/1", null, List.of("ADMIN")),
                Arguments.of("POST", "/api/tournaments/1/teams", "{\"teamId\":2}",
                        List.of("ADMIN", "MANAGER")),
                Arguments.of("DELETE", "/api/tournaments/1/teams/2", null, List.of("ADMIN", "MANAGER")),

                Arguments.of("POST", "/api/matches", "{\"homeTeamId\":1,\"awayTeamId\":2}",
                        List.of("ADMIN", "MANAGER")),
                Arguments.of("PUT", "/api/matches/1", "{\"homeTeamId\":1,\"awayTeamId\":2}",
                        List.of("ADMIN", "MANAGER")),
                Arguments.of("DELETE", "/api/matches/1", null, List.of("ADMIN")),
                Arguments.of("PUT", "/api/matches/1/finish", "{\"score\":\"3:1\"}", List.of("ADMIN", "REFEREE")),
                Arguments.of("POST", "/api/matches/process", "{\"matchIds\":[1]}", List.of("ADMIN"))
        );
    }

    /** Запрос без учётных данных: 401 и единый формат ошибки. */
    @Test
    @WithAnonymousUser
    @DisplayName("без входа: GET /api/teams возвращает 401 в формате ErrorResponse")
    void requestWithoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.suggestion").isNotEmpty());
    }

    /** Пользователь с ролью PLAYER не может удалить пользователя: 403, не 500. */
    @Test
    @WithMockUser(username = "player@test.com", roles = "PLAYER")
    @DisplayName("игрок: DELETE /api/users/1 возвращает 403, а не 500")
    void deleteUserAsPlayer_shouldReturnForbiddenWithErrorResponse() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.suggestion").isNotEmpty());
    }

    /** Проверка матрицы: каждая роль получает 403 на операции, которые ей не разрешены. */
    @ParameterizedTest(name = "{0} {1} запрещено роли {3}")
    @MethodSource("protectedOperations")
    @DisplayName("запрещённая роль получает 403 без вызова сервиса")
    void operationWithWrongRole_shouldReturnForbidden(String method, String path, String body,
                                                      List<String> allowedRoles) throws Exception {
        String role = roleNotAllowed(allowedRoles);
        mockMvc.perform(request(method, path, body).with(asUser(role)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
    }

    /** Проверка матрицы: каждая разрешённая роль выполняет операцию успешно. */
    @ParameterizedTest(name = "{0} {1} разрешено роли {3}")
    @MethodSource("protectedOperations")
    @DisplayName("разрешённая роль выполняет операцию успешно")
    void operationWithAllowedRole_shouldNotBeForbidden(String method, String path, String body,
                                                       List<String> allowedRoles) throws Exception {
        stubServices();
        // Проверяем самую «слабую» разрешённую роль — последнюю в списке.
        String role = allowedRoles.get(allowedRoles.size() - 1);
        mockMvc.perform(request(method, path, body).with(asUser(role)))
                .andExpect(status().is2xxSuccessful());
    }

    /** Тестовый пользователь с заданной ролью (без обращения к базе данных). */
    private RequestPostProcessor asUser(String role) {
        return SecurityMockMvcRequestPostProcessors.user(role.toLowerCase() + "@test.com").roles(role);
    }

    /** Заглушки сервисного слоя, чтобы успешный запрос дошёл до контроллера. */
    private void stubServices() {
        UserResponse user = sampleUser();
        TeamResponse team = sampleTeam();
        TournamentResponse tournament = sampleTournament();
        MatchResponse match = sampleMatch();
        when(userService.create(any(UserRequest.class))).thenReturn(user);
        when(userService.findAll()).thenReturn(List.of(user));
        when(userService.update(anyLong(), any(UserRequest.class))).thenReturn(user);
        when(userService.findByFilter(any())).thenReturn(List.of(user));
        when(teamService.create(any(TeamRequest.class))).thenReturn(team);
        when(teamService.update(anyLong(), any(TeamRequest.class))).thenReturn(team);
        when(teamService.addPlayer(anyLong(), anyLong())).thenReturn(team);
        when(teamService.removePlayer(anyLong(), anyLong())).thenReturn(team);
        when(teamService.appointCaptain(anyLong(), anyLong())).thenReturn(team);
        when(tournamentService.create(any(TournamentRequest.class))).thenReturn(tournament);
        when(tournamentService.update(anyLong(), any(TournamentRequest.class))).thenReturn(tournament);
        when(tournamentService.registerTeam(anyLong(), anyLong())).thenReturn(tournament);
        when(tournamentService.unregisterTeam(anyLong(), anyLong())).thenReturn(tournament);
        when(matchService.create(any(MatchRequest.class))).thenReturn(match);
        when(matchService.update(anyLong(), any(MatchRequest.class))).thenReturn(match);
        when(matchService.finishMatch(anyLong(), any(FinishMatchRequest.class))).thenReturn(match);
        when(matchService.processMatchesConcurrently(any(BatchMatchRequest.class))).thenReturn(List.of(match));
    }

    /** Роль, которой операция не разрешена (для проверки 403). */
    private String roleNotAllowed(List<String> allowedRoles) {
        for (String candidate : List.of("PLAYER", "CAPTAIN", "COACH", "REFEREE", "MANAGER", "ADMIN")) {
            if (!allowedRoles.contains(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Для операции не нашлось запрещённой роли: " + allowedRoles);
    }

    /** Сборка запроса нужного HTTP-метода с телом, если оно есть. */
    private MockHttpServletRequestBuilder request(String method, String path, String body) {
        MockHttpServletRequestBuilder builder = switch (method) {
            case "POST" -> post(path);
            case "PUT" -> put(path);
            case "DELETE" -> delete(path);
            default -> get(path);
        };
        if (body != null) {
            builder.contentType(MediaType.APPLICATION_JSON).content(body);
        }
        return builder;
    }
}
