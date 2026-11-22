package com.tournament.support;
import com.tournament.service.MatchService;
import com.tournament.service.TeamService;
import com.tournament.service.TournamentService;
import com.tournament.service.UserService;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
/**
 * База для тестов REST-контроллеров.
 *
 * <p>Репозитории подменены моками (см. {@link AbstractDatabaseMockSupport}), сервисный слой
 * тоже подменён моками: проверяется только слой Controller (маршруты, коды ответов, JSON).
 * База данных не используется.</p>
 */
@AutoConfigureMockMvc
public abstract class AbstractServiceMockSupport extends AbstractDatabaseMockSupport {
    @MockitoBean
    protected UserService userService;
    @MockitoBean
    protected TeamService teamService;
    @MockitoBean
    protected TournamentService tournamentService;
    @MockitoBean
    protected MatchService matchService;
}
