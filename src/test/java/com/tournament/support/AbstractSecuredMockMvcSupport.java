package com.tournament.support;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * База для HTTP-тестов защищённого REST API (лабораторная работа №4).
 *
 * <p>После подключения Spring Security каждый запрос MockMvc должен выполняться
 * от имени пользователя. Аннотация {@link WithMockUser} на уровне класса подставляет
 * тестового администратора — у него есть права на все операции, поэтому старые тесты
 * проверяют маршрутизацию и коды ответов, не занимаясь правами доступа.</p>
 *
 * <p>Права доступа к отдельным операциям проверяются отдельно: тестами 401/403
 * и параметризованным тестом матрицы ролей (см. {@code SecurityAccessTest}).</p>
 */
@AutoConfigureMockMvc
@WithMockUser(username = "admin@test.com", roles = "ADMIN")
public abstract class AbstractSecuredMockMvcSupport extends AbstractServiceMockSupport {
}
