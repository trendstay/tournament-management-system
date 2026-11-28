package com.tournament.security;

import com.tournament.support.AbstractSecuredMockMvcSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Проверка открытых адресов (лабораторная работа №4).
 *
 * <p>По заданию Swagger UI и OpenAPI JSON должны открываться без входа,
 * а методы API — требовать аутентификацию.</p>
 */
@DisplayName("Spring Security: документация открыта, API защищён")
class PublicDocumentationAccessTest extends AbstractSecuredMockMvcSupport {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithAnonymousUser
    @DisplayName("OpenAPI JSON /v3/api-docs доступен без входа")
    void apiDocs_shouldBePublic() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("страница Swagger UI доступна без входа")
    void swaggerUi_shouldBePublic() throws Exception {
        // Адрес /swagger-ui.html перенаправляет на страницу Swagger UI.
        // Главное в проверке: Security не требует входа (не 401), а отдаёт перенаправление.
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("метод API без входа возвращает 401")
    void apiMethod_shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isUnauthorized());
    }
}
