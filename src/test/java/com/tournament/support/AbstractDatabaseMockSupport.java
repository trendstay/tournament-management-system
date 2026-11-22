package com.tournament.support;
import com.tournament.repository.MatchRepository;
import com.tournament.repository.PlayerUserRepository;
import com.tournament.repository.TeamRepository;
import com.tournament.repository.TournamentRepository;
import com.tournament.repository.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
/**
 * Базовый класс интеграционных тестов приложения.
 *
 * <p>Все репозитории Spring Data JPA подменяются Mockito-моками, а автоконфигурации
 * источника данных, Hibernate и JPA отключены в {@code src/test/resources/application.properties}.
 * Поэтому Spring-контекст поднимается <b>без подключения к базе данных</b>, и ни один
 * тест не читает и не изменяет данные в БД.</p>
 */
@SpringBootTest
public abstract class AbstractDatabaseMockSupport {
    @MockitoBean
    protected UserRepository userRepository;
    @MockitoBean
    protected PlayerUserRepository playerUserRepository;
    @MockitoBean
    protected TeamRepository teamRepository;
    @MockitoBean
    protected TournamentRepository tournamentRepository;
    @MockitoBean
    protected MatchRepository matchRepository;
}
