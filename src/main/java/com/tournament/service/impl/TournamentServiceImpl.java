package com.tournament.service.impl;
import com.tournament.exception.AccessDeniedException;
import com.tournament.exception.BusinessRuleException;
import com.tournament.exception.ConflictException;
import com.tournament.exception.NotFoundException;
import com.tournament.exception.ValidationException;
import com.tournament.dto.tournament.TournamentFilter;
import com.tournament.dto.tournament.TournamentRequest;
import com.tournament.dto.tournament.TournamentResponse;
import com.tournament.mapper.TournamentMapper;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import com.tournament.model.user.RefereeUser;
import com.tournament.model.user.User;
import com.tournament.repository.MatchRepository;
import com.tournament.repository.TeamRepository;
import com.tournament.repository.TournamentRepository;
import com.tournament.repository.UserRepository;
import com.tournament.service.TournamentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
/**
 * Реализация сервиса турниров: CRUD, регистрация команд, турнирная таблица, фильтры.
 */
@Service
@Transactional
public class TournamentServiceImpl extends AbstractCrudService<Tournament, TournamentRequest, TournamentResponse>
        implements TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final TournamentMapper tournamentMapper;
    public TournamentServiceImpl(TournamentRepository tournamentRepository,
                                 TeamRepository teamRepository,
                                 UserRepository userRepository,
                                 MatchRepository matchRepository,
                                 TournamentMapper tournamentMapper) {
        super(tournamentRepository, tournamentMapper);
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
        this.tournamentMapper = tournamentMapper;
    }
    @Override
    @Transactional(readOnly = true)
    public List<TournamentResponse> findByFilter(TournamentFilter filter) {
        if (filter == null || filter.isEmpty()) {
            return toResponseList(tournamentRepository.findAll());
        }
        if (filter.getSportType() != null) {
            return tournamentMapper.toResponseList(tournamentRepository.findBySportType(filter.getSportType()));
        }
        if (filter.getTitle() != null && !filter.getTitle().isBlank()) {
            return tournamentMapper.toResponseList(tournamentRepository.findByTitleContainingIgnoreCase(filter.getTitle()));
        }
        if (filter.getStartFrom() != null || filter.getStartTo() != null) {
            if (filter.getStartFrom() == null || filter.getStartTo() == null) {
                throw new ValidationException("Для фильтра по дате необходимы обе границы: startFrom и startTo",
                        "Передайте начальную и конечную дату турнира");
            }
            return tournamentMapper.toResponseList(
                    tournamentRepository.findByStartDateBetween(filter.getStartFrom(), filter.getStartTo()));
        }
        if (filter.getRefereeId() == null) {
            throw new ValidationException("Не задан ни один критерий фильтра турниров",
                    "Укажите вид спорта, название, обе границы дат или ID судьи");
        }
        return tournamentMapper.toResponseList(tournamentRepository.findByRefereeId(filter.getRefereeId()));
    }
    @Override
    public TournamentResponse registerTeam(Long tournamentId, Long teamId) {
        Tournament tournament = findEntity(tournamentId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Команда с ID " + teamId + " не найдена"));
        boolean alreadyRegistered = tournament.getTeams().stream()
                .anyMatch(registered -> registered.getId().equals(teamId));
        if (alreadyRegistered) {
            throw new ConflictException(
                    "Команда с ID " + teamId + " уже участвует в турнире",
                    "Выберите другую команду или сначала исключите эту команду из турнира",
                    "TEAM_ALREADY_REGISTERED");
        }
        tournament.addTeam(team);
        return toResponse(tournamentRepository.save(tournament));
    }
    @Override
    public TournamentResponse unregisterTeam(Long tournamentId, Long teamId) {
        Tournament tournament = findEntity(tournamentId);
        boolean removed = tournament.getTeams().removeIf(registered -> registered.getId().equals(teamId));
        if (!removed) {
            throw new ConflictException(
                    "Команда с ID " + teamId + " не участвует в турнире",
                    "Проверьте идентификаторы турнира и команды: исключить можно только зарегистрированную команду",
                    "TEAM_NOT_REGISTERED");
        }
        return toResponse(tournamentRepository.save(tournament));
    }
    @Override
    @Transactional(readOnly = true)
    public String getStandingsTable(Long tournamentId) {
        Tournament tournament = findEntity(tournamentId);
        return tournament.generateStandingsTable(matchRepository.findByTournamentId(tournamentId));
    }
    @Override
    public void delete(Long id) {
        Tournament tournament = findEntity(id);
        long matchesCount = matchRepository.countByTournamentId(id);
        if (matchesCount > 0) {
            throw new BusinessRuleException(
                    "Нельзя удалить турнир с ID " + id + ": к нему привязано матчей — " + matchesCount,
                    "Сначала удалите или перенесите матчи этого турнира, затем повторите удаление",
                    "TOURNAMENT_HAS_MATCHES");
        }
        tournamentRepository.delete(tournament);
    }
    @Override
    protected Tournament createEntity(TournamentRequest request) {
        Tournament tournament = new Tournament(
                null,
                request.getTitle(),
                request.getSportType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getPrizePool()
        );
        applyReferee(tournament, request.getRefereeId());
        applyTeams(tournament, request.getTeamIds());
        validateDates(tournament);
        return tournament;
    }
    @Override
    protected void applyRequest(Tournament entity, TournamentRequest request) {
        entity.setTitle(request.getTitle());
        entity.setSportType(request.getSportType());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setPrizePool(request.getPrizePool());
        applyReferee(entity, request.getRefereeId());
        applyTeams(entity, request.getTeamIds());
        validateDates(entity);
    }
    @Override
    protected String entityName() {
        return "Турнир";
    }
    private void applyReferee(Tournament tournament, Long refereeId) {
        if (refereeId == null) {
            tournament.setReferee(null);
            return;
        }
        User user = userRepository.findById(refereeId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + refereeId + " не найден"));
        if (!(user instanceof RefereeUser)) {
            throw new AccessDeniedException("назначение судьёй турнира",
                    "пользователь с ID " + refereeId + " не является судьёй",
                    "Укажите пользователя с ролью REFEREE или выберите другого судью");
        }
        tournament.setReferee((RefereeUser) user);
    }
    private void applyTeams(Tournament tournament, List<Long> teamIds) {
        tournament.getTeams().clear();
        if (teamIds == null || teamIds.isEmpty()) {
            return;
        }
        for (Long teamId : teamIds) {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new NotFoundException("Команда с ID " + teamId + " не найдена"));
            tournament.addTeam(team);
        }
    }
    private void validateDates(Tournament tournament) {
        if (tournament.getStartDate() != null
                && tournament.getEndDate() != null
                && tournament.getEndDate().isBefore(tournament.getStartDate())) {
            throw new BusinessRuleException(
                    "Дата окончания турнира " + tournament.getEndDate()
                            + " не может быть раньше даты начала " + tournament.getStartDate(),
                    "Укажите дату окончания, которая совпадает с датой начала или следует за ней",
                    "TOURNAMENT_DATES_INVALID");
        }
    }
}
