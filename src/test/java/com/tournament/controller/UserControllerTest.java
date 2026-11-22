package com.tournament.controller;
import com.tournament.exception.NotFoundException;
import com.tournament.model.enums.UserRole;
import com.tournament.dto.user.UserRequest;
import com.tournament.dto.user.UserResponse;
import com.tournament.support.AbstractServiceMockSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Тесты REST-контроллера пользователей.
 * Сервис подменён Mockito-моком, база данных не используется.
 */
@DisplayName("UserController: REST API пользователей")
class UserControllerTest extends AbstractServiceMockSupport {
    @Autowired
    private MockMvc mockMvc;
    private UserResponse sampleUser() {
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setEmail("coach@test.com");
        response.setFirstName("Сергей");
        response.setLastName("Тренеров");
        response.setRole(UserRole.COACH);
        return response;
    }
    @Test
    @DisplayName("POST /api/users возвращает 201 и созданного пользователя")
    void create_shouldReturnCreatedUser() throws Exception {
        when(userService.create(any(UserRequest.class))).thenReturn(sampleUser());
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"coach@test.com\",\"password\":\"pass123\","
                                + "\"firstName\":\"Сергей\",\"lastName\":\"Тренеров\",\"role\":\"COACH\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("coach@test.com"))
                .andExpect(jsonPath("$.role").value("COACH"));
    }
    @Test
    @DisplayName("POST /api/users с некорректным email возвращает 400")
    void create_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-an-email\",\"password\":\"pass123\","
                                + "\"firstName\":\"Сергей\",\"lastName\":\"Тренеров\",\"role\":\"COACH\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ARGUMENT_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.email").value("некорректный формат email"));
    }
    @Test
    @DisplayName("GET /api/users/{id} возвращает пользователя")
    void findById_shouldReturnUser() throws Exception {
        when(userService.findById(1L)).thenReturn(sampleUser());
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Сергей"));
    }
    @Test
    @DisplayName("GET /api/users возвращает список пользователей")
    void findAll_shouldReturnList() throws Exception {
        when(userService.findAll()).thenReturn(List.of(sampleUser()));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    @Test
    @DisplayName("GET /api/users/search фильтрует по роли")
    void search_byRole_shouldReturnFilteredUsers() throws Exception {
        when(userService.findByFilter(any())).thenReturn(List.of(sampleUser()));
        mockMvc.perform(get("/api/users/search").param("role", "COACH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    @Test
    @DisplayName("PUT /api/users/{id} обновляет пользователя")
    void update_shouldReturnUpdatedUser() throws Exception {
        when(userService.update(eq(1L), any(UserRequest.class))).thenReturn(sampleUser());
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"coach@test.com\",\"password\":\"pass123\","
                                + "\"firstName\":\"Сергей\",\"lastName\":\"Тренеров\",\"role\":\"COACH\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
    @Test
    @DisplayName("DELETE /api/users/{id} возвращает 204")
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
        verify(userService).delete(1L);
    }
    @Test
    @DisplayName("GET /api/users/{id}: отсутствующий пользователь возвращает 404 с подсказкой")
    void findById_whenMissing_shouldReturnNotFoundResponse() throws Exception {
        when(userService.findById(99L)).thenThrow(new NotFoundException("Пользователь с ID 99 не найден"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.suggestion").isNotEmpty());
    }
    @Test
    @DisplayName("GET /api/users/{id}: нечисловой ID возвращает 400")
    void findById_withNonNumericId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/users/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ARGUMENT_TYPE_MISMATCH"));
    }
    @Test
    @DisplayName("POST /api/users: некорректный JSON возвращает 400")
    void create_withMalformedJson_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("MALFORMED_JSON"));
    }
    @Test
    @DisplayName("GET /api/users/{id}: непредвиденная ошибка возвращает 500")
    void findById_withUnexpectedFailure_shouldReturnInternalServerError() throws Exception {
        when(userService.findById(500L)).thenThrow(new IllegalStateException("ошибка сервиса"));

        mockMvc.perform(get("/api/users/500"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_ERROR"));
    }
}
