package com.tournament.service.impl;
import com.tournament.exception.AccessDeniedException;
import com.tournament.exception.BusinessRuleException;
import com.tournament.exception.ConflictException;
import com.tournament.exception.NotFoundException;
import com.tournament.exception.ValidationException;
import com.tournament.dto.user.UserFilter;
import com.tournament.dto.user.UserRequest;
import com.tournament.dto.user.UserResponse;
import com.tournament.mapper.UserMapper;
import com.tournament.model.enums.UserRole;
import com.tournament.model.team.Team;
import com.tournament.model.user.PlayerUser;
import com.tournament.model.user.User;
import com.tournament.repository.TeamRepository;
import com.tournament.repository.UserRepository;
import com.tournament.service.UserService;
import com.tournament.service.factory.UserFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
/**
 * Реализация сервиса пользователей.
 *
 * <p>Класс отвечает только за бизнес-логику пользователей: проверку уникальности email,
 * создание нужного подкласса пользователя через {@link UserFactory} и выборку по фильтрам.
 * Пароль сохраняется в поле {@code passwordHash} как есть — хеширование и аутентификация
 * появятся в лабораторной работе №4 (Spring Security).</p>
 */
@Service
@Transactional
public class UserServiceImpl extends AbstractCrudService<User, UserRequest, UserResponse> implements UserService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserFactory userFactory;
    private final UserMapper userMapper;
    public UserServiceImpl(UserRepository userRepository,
                           TeamRepository teamRepository,
                           UserFactory userFactory,
                           UserMapper userMapper) {
        super(userRepository, userMapper);
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.userFactory = userFactory;
        this.userMapper = userMapper;
    }
    @Override
    public List<UserResponse> findByFilter(UserFilter filter) {
        if (filter == null || filter.isEmpty()) {
            return toResponseList(userRepository.findAll());
        }
        boolean hasRole = filter.getRole() != null;
        boolean hasLastName = filter.getLastName() != null && !filter.getLastName().isBlank();
        if (hasRole && hasLastName) {
            return userMapper.toResponseList(
                    userRepository.findByRoleAndLastNameContainingIgnoreCase(filter.getRole(), filter.getLastName()));
        }
        if (hasRole) {
            return userMapper.toResponseList(userRepository.findByRole(filter.getRole()));
        }
        return userMapper.toResponseList(userRepository.findByLastNameContainingIgnoreCase(filter.getLastName()));
    }
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAllByRole(UserRole role) {
        return userMapper.toResponseList(userRepository.findByRole(role));
    }
    @Override
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с email " + email + " не найден",
                        "Проверьте введённый email или сначала зарегистрируйте пользователя"));
        return userMapper.toResponse(user);
    }
    @Override
    public void delete(Long id) {
        User user = findEntity(id);
        if (user instanceof PlayerUser && ((PlayerUser) user).isCaptain()) {
            throw new AccessDeniedException("удаление пользователя",
                    "пользователь с ID " + id + " является капитаном команды",
                    "Назначьте другого капитана или снимите капитанство перед удалением пользователя");
        }
        if (!teamRepository.findByCoachId(id).isEmpty()) {
            throw new AccessDeniedException("удаление пользователя",
                    "пользователь с ID " + id + " является тренером команды",
                    "Назначьте команде другого тренера перед удалением пользователя");
        }
        userRepository.delete(user);
    }
    @Override
    protected User createEntity(UserRequest request) {
        validateEmailUnique(request.getEmail(), null);
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidationException("Пароль обязателен при создании пользователя",
                    "Передайте непустой пароль длиной от 6 до 100 символов");
        }
        String passwordHash = request.getPassword();
        User user = userFactory.create(
                request.getRole(),
                request.getEmail(),
                passwordHash,
                request.getFirstName(),
                request.getLastName()
        );
        applyRole(user, request.getRole());
        applyPlayerData(user, request);
        return user;
    }
    @Override
    protected void applyRequest(User entity, UserRequest request) {
        validateEmailUnique(request.getEmail(), entity.getId());
        entity.setEmail(request.getEmail());
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            entity.setPasswordHash(request.getPassword());
        }
        applyRole(entity, request.getRole());
        applyPlayerData(entity, request);
    }
    @Override
    protected String entityName() {
        return "Пользователь";
    }
    /**
     * Email должен быть уникальным (без учёта регистра).
     * Значение {@code currentUserId} позволяет при обновлении не считать «дубликатом»
     * самого пользователя; для операции создания передаётся {@code null}.
     */
    private void validateEmailUnique(String email, Long currentUserId) {
        if (email == null || !email.contains("@")) {
            throw new ValidationException("Некорректный формат email: " + email,
                    "Укажите email-адрес с символом @ в допустимом формате");
        }
        boolean emailTaken = userRepository.findByEmailIgnoreCase(email)
                .filter(existing -> currentUserId == null || !currentUserId.equals(existing.getId()))
                .isPresent();
        if (emailTaken) {
            throw new ConflictException(
                    "Пользователь с email " + email + " уже существует",
                    "Укажите другой email или откройте существующую учётную запись и измените её",
                    "USER_EMAIL_EXISTS");
        }
    }
    /**
     * Для игроков поддерживается переключение ролей PLAYER ↔ CAPTAIN.
     * Прочие смены ролей запрещены: подклассы пользователей имеют разный набор полей.
     */
    private void applyRole(User entity, UserRole requestedRole) {
        boolean playerRoleRequested = requestedRole == UserRole.PLAYER || requestedRole == UserRole.CAPTAIN;
        if (entity instanceof PlayerUser && playerRoleRequested) {
            PlayerUser player = (PlayerUser) entity;
            player.setCaptain(requestedRole == UserRole.CAPTAIN);
            return;
        }
        if (entity.getRole() != requestedRole) {
            throw new BusinessRuleException(
                    "Смена роли с " + entity.getRole() + " на " + requestedRole
                            + " не поддерживается: тип пользователя не соответствует новой роли",
                    "Создайте отдельную учётную запись с нужной ролью; для игрока доступно переключение PLAYER и CAPTAIN",
                    "USER_ROLE_CHANGE_NOT_SUPPORTED");
        }
    }
    /** Заполнение полей, специфичных для игрока, и привязка к команде. */
    private void applyPlayerData(User user, UserRequest request) {
        if (!(user instanceof PlayerUser)) {
            return;
        }
        PlayerUser player = (PlayerUser) user;
        player.setPhoneNumber(request.getPhoneNumber());
        Team currentTeam = player.getTeam();
        if (request.getTeamId() == null) {
            if (currentTeam != null) {
                currentTeam.removePlayer(player);
            }
            return;
        }
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new NotFoundException(
                        "Команда с ID " + request.getTeamId() + " не найдена"));
        if (currentTeam != null && !currentTeam.getId().equals(team.getId())) {
            currentTeam.removePlayer(player);
        }
        team.addPlayer(player);
    }
}
