package com.tournament.support;
/**
 * База для тестов сервисного слоя.
 *
 * <p>Репозитории подменены моками (см. {@link AbstractDatabaseMockSupport}), а сервисы,
 * фабрики, мапперы и доменная логика — настоящие. Проверяется именно код приложения,
 * база данных не используется.</p>
 */
public abstract class AbstractRepositoryMockSupport extends AbstractDatabaseMockSupport {
}
