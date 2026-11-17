package com.tournament.service;
import com.tournament.dto.user.UserFilter;
import com.tournament.dto.user.UserRequest;
import com.tournament.dto.user.UserResponse;
import com.tournament.model.enums.UserRole;
import com.tournament.model.team.Team;
import com.tournament.model.user.AdminUser;
import com.tournament.model.user.CoachUser;
import com.tournament.model.user.PlayerUser;
import com.tournament.model.user.User;
import com.tournament.support.AbstractRepositoryMockSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * Тесты сервиса пользователей. База данных не используется — репозитории замоканы.
 */
@DisplayName("UserService: CRUD и фильтры пользователей")
class UserServiceImplTest extends AbstractRepositoryMockSupport {
    @Autowired
    private UserService userService;
    @Test
    @DisplayName("create: создаёт администратора с ролью ADMIN")
    void create_shouldCreateAdminUser() {
        UserRequest request = new UserRequest("admin@test.com", "pass123", "Иван", "Иванов", UserRole.ADMIN, null, null);
        when(userRepository.findByEmailIgnoreCase("admin@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        UserResponse response = userService.create(request);
        assertEquals(1L, response.getId());
        assertEquals(UserRole.ADMIN, response.getRole());
        assertEquals("admin@test.com", response.getEmail());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertInstanceOf(AdminUser.class, savedUser);
    }
    @Test
    @DisplayName("create: дубликат email приводит к IllegalArgumentException")
    void create_withDuplicateEmail_shouldThrowException() {
        User existing = new AdminUser(5L, "dup@test.com", "hash", "A", "B");
        when(userRepository.findByEmailIgnoreCase("dup@test.com")).thenReturn(Optional.of(existing));
        UserRequest request = new UserRequest("dup@test.com", "pass123", "C", "D", UserRole.MANAGER, null, null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.create(request));
        assertTrue(exception.getMessage().contains("уже существует"));
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    @DisplayName("create: игрок привязывается к команде")
    void create_playerWithTeam_shouldAttachPlayerToTeam() {
        Team team = new Team(3L, "Зенит", "Санкт-Петербург");
        when(teamRepository.findById(3L)).thenReturn(Optional.of(team));
        when(userRepository.findByEmailIgnoreCase("player@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserRequest request = new UserRequest("player@test.com", "pass123", "Пётр", "Петров",
                UserRole.PLAYER, "+79990000000", 3L);
        UserResponse response = userService.create(request);
        assertEquals(3L, response.getTeamId());
        assertFalse(response.isCaptain());
        assertEquals(1, team.getPlayers().size());
        assertEquals("player@test.com", team.getPlayers().get(0).getEmail());
    }
    @Test
    @DisplayName("create: роль CAPTAIN делает игрока капитаном")
    void create_captainRole_shouldMarkPlayerAsCaptain() {
        when(userRepository.findByEmailIgnoreCase("captain@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserRequest request = new UserRequest("captain@test.com", "pass123", "Пётр", "Петров",
                UserRole.CAPTAIN, null, null);
        UserResponse response = userService.create(request);
        assertTrue(response.isCaptain());
        assertEquals(UserRole.CAPTAIN, response.getRole());
    }
    @Test
    @DisplayName("findById: отсутствующий пользователь приводит к IllegalArgumentException")
    void findById_whenMissing_shouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.findById(99L));
    }
    @Test
    @DisplayName("update: запрещает смену роли тренера на администратора")
    void update_withUnsupportedRoleChange_shouldThrowException() {
        CoachUser coach = new CoachUser(7L, "coach@test.com", "hash", "Иван", "Иванов");
        when(userRepository.findById(7L)).thenReturn(Optional.of(coach));
        when(userRepository.findByEmailIgnoreCase("coach@test.com")).thenReturn(Optional.of(coach));
        UserRequest request = new UserRequest("coach@test.com", null, "Иван", "Иванов", UserRole.ADMIN, null, null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.update(7L, request));
        assertTrue(exception.getMessage().contains("не поддерживается"));
    }
    @Test
    @DisplayName("update: обновляет имя и телефон игрока")
    void update_player_shouldChangeFields() {
        PlayerUser player = new PlayerUser(8L, "player@test.com", "hash", "Пётр", "Петров", "+70000000000");
        when(userRepository.findById(8L)).thenReturn(Optional.of(player));
        when(userRepository.findByEmailIgnoreCase("player@test.com")).thenReturn(Optional.of(player));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserRequest request = new UserRequest("player@test.com", null, "Алексей", "Игроков",
                UserRole.PLAYER, "+79991234567", null);
        UserResponse response = userService.update(8L, request);
        assertEquals("Алексей", response.getFirstName());
        assertEquals("+79991234567", response.getPhoneNumber());
    }
    @Test
    @DisplayName("delete: удаляет пользователя через репозиторий")
    void delete_shouldRemoveUser() {
        User user = new CoachUser(4L, "coach@test.com", "hash", "Сергей", "Тренеров");
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        userService.delete(4L);
        verify(userRepository).delete(user);
    }
    @Test
    @DisplayName("findByFilter: выборка по роли")
    void findByFilter_byRole_shouldUseRoleQuery() {
        User coach = new CoachUser(4L, "coach@test.com", "hash", "Сергей", "Тренеров");
        when(userRepository.findByRole(UserRole.COACH)).thenReturn(List.of(coach));
        UserFilter filter = new UserFilter();
        filter.setRole(UserRole.COACH);
        List<UserResponse> result = userService.findByFilter(filter);
        assertEquals(1, result.size());
        assertEquals("coach@test.com", result.get(0).getEmail());
    }
    @Test
    @DisplayName("findByFilter: пустой фильтр возвращает всех пользователей")
    void findByFilter_withoutConditions_shouldReturnAll() {
        when(userRepository.findAll()).thenReturn(List.of(
                new AdminUser(1L, "admin@test.com", "hash", "Иван", "Иванов"),
                new CoachUser(2L, "coach@test.com", "hash", "Пётр", "Петров")
        ));
        List<UserResponse> result = userService.findByFilter(new UserFilter());
        assertEquals(2, result.size());
    }
}
