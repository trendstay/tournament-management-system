package com.tournament.service;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Результат многопоточной обработки коллекции матчей.
 * <p>
 * Хранит количество обработанных матчей, имена потоков, которые реально
 * участвовали в работе, и затраченное время. По именам потоков можно
 * убедиться, что обработка шла параллельно.
 */
public final class MatchProcessingReport {

    private final int processedMatches;
    private final Set<String> workerThreadNames;
    private final long durationMillis;

    public MatchProcessingReport(int processedMatches, Set<String> workerThreadNames, long durationMillis) {
        this.processedMatches = processedMatches;
        this.workerThreadNames = Collections.unmodifiableSet(new TreeSet<>(workerThreadNames));
        this.durationMillis = durationMillis;
    }

    public int getProcessedMatches() {
        return processedMatches;
    }

    public Set<String> getWorkerThreadNames() {
        return workerThreadNames;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    /**
     * Краткое описание результата для вывода в консоль или лог.
     */
    public String getSummary() {
        return String.format("Обработано матчей: %d; потоки: %s; время: %d мс",
                processedMatches, workerThreadNames, durationMillis);
    }
}
