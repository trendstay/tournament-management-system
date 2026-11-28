package com.tournament.service.impl;
import com.tournament.exception.BusinessRuleException;
import com.tournament.exception.NotFoundException;
import com.tournament.exception.ValidationException;
import com.tournament.dto.match.BatchMatchRequest;
import com.tournament.dto.match.FinishMatchRequest;
import com.tournament.dto.match.MatchFilter;
import com.tournament.dto.match.MatchRequest;
import com.tournament.dto.match.MatchResponse;
import com.tournament.mapper.MatchMapper;
import com.tournament.model.enums.MatchStatus;
import com.tournament.model.match.Match;
import com.tournament.model.team.Team;
import com.tournament.model.tournament.Tournament;
import com.tournament.repository.MatchRepository;
import com.tournament.repository.TeamRepository;
import com.tournament.repository.TournamentRepository;
import com.tournament.service.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * Реализация сервиса матчей: CRUD, внесение результата, фильтры и
 * многопоточная (пакетная) обработка коллекции матчей из лабораторной работы №1.
 */
@Service
@Transactional
public class MatchServiceImpl extends AbstractCrudService<Match, MatchRequest, MatchResponse> implements MatchService {
    private static final Logger log = LoggerFactory.getLogger(MatchServiceImpl.class);
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final MatchMapper matchMapper;
    public MatchServiceImpl(MatchRepository matchRepository,
                            TeamRepository teamRepository,
                            TournamentRepository tournamentRepository,
                            MatchMapper matchMapper) {
        super(matchRepository, matchMapper);
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.tournamentRepository = tournamentRepository;
        this.matchMapper = matchMapper;
    }
    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> findByFilter(MatchFilter filter) {
        if (filter == null || filter.isEmpty()) {
            return toResponseList(matchRepository.findAll());
        }
        if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
            MatchStatus status = parseStatus(filter.getStatus());
            return matchMapper.toResponseList(matchRepository.findByStatus(status));
        }
        if (filter.getTournamentId() != null) {
            return matchMapper.toResponseList(matchRepository.findByTournamentId(filter.getTournamentId()));
        }
        if (filter.getTeamId() != null) {
            return matchMapper.toResponseList(matchRepository.findByTeamId(filter.getTeamId()));
        }
        if (filter.getFrom() == null || filter.getTo() == null) {
            throw new ValidationException("Для фильтра по дате необходимы обе границы: from и to",
                    "Передайте начальную и конечную дату; например, from=2026-01-01T00:00:00 и to=2026-12-31T23:59:59");
        }
        return matchMapper.toResponseList(matchRepository.findByMatchDateBetween(filter.getFrom(), filter.getTo()));
    }
    @Override
    public MatchResponse finishMatch(Long matchId, FinishMatchRequest request) {
        Match match = findEntity(matchId);
        match.updateScore(request.getScore());
        Tournament tournament = match.getTournament();
        if (tournament != null && tournament.getSportType() != null
                && !tournament.getSportType().isAllowsDraw() && match.isDraw()) {
            throw new BusinessRuleException(
                    "Ничья недопустима в дисциплине " + tournament.getSportType().getDisplayName()
                            + " для матча с ID " + matchId,
                    "Измените счёт или назначьте дополнительное время согласно правилам дисциплины",
                    "DRAW_NOT_ALLOWED");
        }
        match.setStatus(MatchStatus.FINISHED);
        MatchResponse response = toResponse(matchRepository.save(match));
        log.info("Матч завершён: id={}, счёт={}, победитель={}",
                matchId, response.getScore(), response.getWinnerTeamName());
        return response;
    }
    /**
     * Пакетное завершение матчей с распараллеливанием расчётов.
     *
     * <p>Тяжёлая часть (обработка результата) выполняется в пуле из трёх именованных
     * потоков, а запись в базу данных — последовательно в одной транзакции, чтобы
     * не нарушать требования JPA к потокам.</p>
     */
    @Override
    public List<MatchResponse> processMatchesConcurrently(BatchMatchRequest request) {
        List<Match> matches = new ArrayList<>();
        for (Long matchId : request.getMatchIds()) {
            matches.add(findEntity(matchId));
        }
        ExecutorHolder holder = new ExecutorHolder(3);
        ExecutorService executor = holder.getExecutor();
        try {
            for (Match match : matches) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        // Имя потока в логе показывает, что расчёт идёт параллельно.
                        log.info("[{}] Расчёт результата матча ID: {}",
                                Thread.currentThread().getName(), match.getId());
                        if (match.getStatus() == null || match.getStatus() != MatchStatus.FINISHED) {
                            match.setStatus(MatchStatus.FINISHED);
                        }
                    }
                });
            }
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
        List<MatchResponse> result = matchMapper.toResponseList(matchRepository.saveAll(matches));
        log.info("Пакетная обработка завершена: матчей={}", result.size());
        return result;
    }
    @Override
    protected Match createEntity(MatchRequest request) {
        Team homeTeam = findTeam(request.getHomeTeamId());
        Team awayTeam = findTeam(request.getAwayTeamId());
        if (homeTeam.getId().equals(awayTeam.getId())) {
            throw new BusinessRuleException(
                    "Команда с ID " + homeTeam.getId() + " не может играть сама с собой",
                    "Укажите две разные команды для матча",
                    "MATCH_TEAMS_MUST_DIFFER");
        }
        Match match = new Match(null, homeTeam, awayTeam, request.getMatchDate());
        match.setTournament(findTournament(request.getTournamentId()));
        if (request.getScore() != null && !request.getScore().isBlank()) {
            match.updateScore(request.getScore());
        }
        return match;
    }
    @Override
    protected void applyRequest(Match entity, MatchRequest request) {
        Team homeTeam = findTeam(request.getHomeTeamId());
        Team awayTeam = findTeam(request.getAwayTeamId());
        if (homeTeam.getId().equals(awayTeam.getId())) {
            throw new BusinessRuleException(
                    "Команда с ID " + homeTeam.getId() + " не может играть сама с собой",
                    "Укажите две разные команды для матча",
                    "MATCH_TEAMS_MUST_DIFFER");
        }
        entity.setHomeTeam(homeTeam);
        entity.setAwayTeam(awayTeam);
        entity.setMatchDate(request.getMatchDate());
        entity.setTournament(findTournament(request.getTournamentId()));
        if (request.getScore() != null && !request.getScore().isBlank()) {
            entity.updateScore(request.getScore());
        }
    }
    @Override
    protected String entityName() {
        return "Матч";
    }
    private MatchStatus parseStatus(String status) {
        try {
            return MatchStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Неизвестный статус матча: " + status,
                    "Используйте один из статусов: SCHEDULED, IN_PROGRESS, FINISHED или CANCELLED");
        }
    }
    private Team findTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Команда с ID " + teamId + " не найдена"));
    }
    private Tournament findTournament(Long tournamentId) {
        if (tournamentId == null) {
            return null;
        }
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NotFoundException("Турнир с ID " + tournamentId + " не найден"));
    }
    /**
     * Вспомогательный класс создания пула именованных потоков.
     * Вынесен ради соблюдения принципа единственной ответственности.
     */
    private static final class ExecutorHolder {
        private final ExecutorService executor;
        private ExecutorHolder(int threadsCount) {
            this.executor = Executors.newFixedThreadPool(threadsCount, new ThreadFactory() {
                private final AtomicInteger threadCounter = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable runnable) {
                    return new Thread(runnable, "MatchWorker-Thread-" + threadCounter.getAndIncrement());
                }
            });
        }
        private ExecutorService getExecutor() {
            return executor;
        }
    }
}
