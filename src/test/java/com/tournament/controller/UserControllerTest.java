package com.tournament.controller;
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
                .andExpect(status().isBadRequest());
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
}
