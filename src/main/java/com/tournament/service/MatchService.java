package com.tournament.service;

import com.tournament.model.enums.MatchStatus;
import com.tournament.model.match.Match;
import com.tournament.repository.MatchRepository;
import com.tournament.util.NamedThreadFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Сервис управления матчами.
 * <p>
 * Дополнительно содержит алгоритм многопоточной обработки коллекции матчей:
 * задачи распределяются между именованными потоками пула, что видно в логе.
 */
public class MatchService {

    private static final Logger LOGGER = Logger.getLogger(MatchService.class.getName());

    private static final int MAX_WORKERS = 3;
    private static final String THREAD_NAME_PREFIX = "match-worker-";
    private static final long SIMULATED_WORK_MILLIS = 100L;
    private static final long THREAD_SYNC_TIMEOUT_SECONDS = 5L;
    private static final long PROCESSING_TIMEOUT_SECONDS = 30L;

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = Objects.requireNonNull(matchRepository, "Репозиторий матчей не может быть null");
    }

    /**
     * Ставит матч в расписание и сохраняет его.
     */
    public Match scheduleMatch(Match match) {
        Objects.requireNonNull(match, "Матч не может быть null");
        return matchRepository.save(match);
    }

    /**
     * Завершает матч: фиксирует счёт и переводит матч в статус
     * {@link MatchStatus#FINISHED}.
     */
    public Match finishMatch(Long matchId, String score) {
        Match match = findById(matchId);
        match.updateScore(score);
        match.setStatus(MatchStatus.FINISHED);
        return matchRepository.save(match);
    }

    /**
     * Находит матч по идентификатору.
     *
     * @throws IllegalArgumentException если матч не найден
     */
    public Match findById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Матч с ID " + id + " не найден"));
    }

    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    public List<Match> findByStatus(MatchStatus status) {
        return matchRepository.findByStatus(status);
    }

    public List<Match> findByTournamentId(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId);
    }

    /**
     * Многопоточная обработка коллекции матчей.
     * <p>
     * Пул из именованных потоков обрабатывает матчи параллельно. Потоки
     * синхронизируются на старте, чтобы в работе гарантированно участвовало
     * несколько потоков — это видно в логе и в отчёте.
     *
     * @param matches коллекция матчей для обработки
     * @return отчёт с количеством обработанных матчей, именами потоков и временем
     */
    public MatchProcessingReport processMatchesConcurrently(List<Match> matches) {
        Objects.requireNonNull(matches, "Коллекция матчей не может быть null");
        if (matches.isEmpty()) {
            return new MatchProcessingReport(0, Collections.emptySet(), 0L);
        }

        int workerCount = Math.min(MAX_WORKERS, matches.size());
        long startNanos = System.nanoTime();
        Set<String> threadNames = ConcurrentHashMap.newKeySet();
        CountDownLatch workersStarted = new CountDownLatch(workerCount);
        ExecutorService executor = Executors.newFixedThreadPool(
                workerCount, new NamedThreadFactory(THREAD_NAME_PREFIX));
        List<Future<?>> futures = new ArrayList<>();

        try {
            for (Match match : matches) {
                futures.add(executor.submit(() -> processMatch(match, threadNames, workersStarted)));
            }
        } finally {
            executor.shutdown();
        }

        awaitTermination(executor);
        collectResults(futures);

        long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
        LOGGER.info(() -> "Многопоточная обработка завершена: " + matches.size()
                + " матчей, потоки " + threadNames);
        return new MatchProcessingReport(matches.size(), threadNames, durationMillis);
    }

    private void processMatch(Match match, Set<String> threadNames, CountDownLatch workersStarted) {
        String threadName = Thread.currentThread().getName();
        threadNames.add(threadName);
        workersStarted.countDown();
        awaitOtherWorkers(workersStarted);

        LOGGER.info(() -> "[" + threadName + "] Начата обработка матча ID=" + match.getId());
        try {
            Thread.sleep(SIMULATED_WORK_MILLIS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            LOGGER.warning(() -> "[" + threadName + "] Обработка матча ID=" + match.getId() + " прервана");
            return;
        }

        match.setStatus(MatchStatus.FINISHED);
        matchRepository.save(match);
        LOGGER.info(() -> "[" + threadName + "] Матч ID=" + match.getId()
                + " переведён в статус " + MatchStatus.FINISHED.getDisplayName());
    }

    private void awaitOtherWorkers(CountDownLatch workersStarted) {
        try {
            workersStarted.await(THREAD_SYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private void awaitTermination(ExecutorService executor) {
        try {
            if (!executor.awaitTermination(PROCESSING_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                throw new IllegalStateException("Не удалось обработать матчи за "
                        + PROCESSING_TIMEOUT_SECONDS + " секунд");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
            throw new IllegalStateException("Многопоточная обработка матчей была прервана", exception);
        }
    }

    private void collectResults(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Ожидание результата обработки матча было прервано", exception);
            } catch (ExecutionException exception) {
                throw new IllegalStateException("Ошибка при обработке матча", exception.getCause());
            }
        }
    }
}
