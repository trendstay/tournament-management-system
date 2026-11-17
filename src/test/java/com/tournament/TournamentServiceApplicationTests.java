package com.tournament;
import com.tournament.repository.MatchRepository;
import com.tournament.repository.PlayerUserRepository;
import com.tournament.repository.TeamRepository;
import com.tournament.repository.TournamentRepository;
import com.tournament.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.junit.jupiter.api.Assertions.assertNotNull;
/**
 * Проверка загрузки Spring-контекста приложения.
 *
 * <p>Репозитории подменены Mockito-моками, поэтому контекст поднимается
 * <b>без подключения к базе данных</b>.</p>
 */
@SpringBootTest
class TournamentServiceApplicationTests {
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private PlayerUserRepository playerUserRepository;
    @MockitoBean
    private TeamRepository teamRepository;
    @MockitoBean
    private TournamentRepository tournamentRepository;
    @MockitoBean
    private MatchRepository matchRepository;
    @Test
    @DisplayName("Spring-контекст приложения загружается")
    void contextLoads() {
        assertNotNull(userRepository);
    }
}
