package com.tournament.service;

import com.tournament.model.enums.MatchStatus;
import com.tournament.model.match.Match;
import com.tournament.repository.MatchRepository;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Сервис управления матчами.
 * Демонстрирует многопоточную распараллеленную обработку коллекции данных
 */
public class MatchService {
    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    /**
     * Завершение матча с установкой счета.
     */
    public Match finishMatch(Long matchId, String score) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Матч не найден с ID: " + matchId));

        match.updateScore(score);
        match.setStatus(MatchStatus.FINISHED);
        return matchRepository.save(match);
    }

    /**
     * Многопоточный метод пакетной обработки коллекции матчей.
     */
    public void processMatchesConcurrently(List<Match> matches) {
        // Создание пула из 3 именованных потоков (ThreadFactory)
        ExecutorService executor = Executors.newFixedThreadPool(3, new ThreadFactory() {
            private final AtomicInteger threadCounter = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "MatchWorker-Thread-" + threadCounter.getAndIncrement());
            }
        });

        // Распределение элементов коллекции по потокам
        for (Match match : matches) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    String threadName = Thread.currentThread().getName();
                    System.out.println("[" + threadName + "] Выполняется расчёт результатов матча ID: " + match.getId());

                    try {
                        Thread.sleep(100); // Имитация вычислений
                        match.setStatus(MatchStatus.FINISHED);
                        matchRepository.save(match);
                        System.out.println("[" + threadName + "] Успешно завершен матч ID: " + match.getId());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        // Завершение работы пула потоков
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}