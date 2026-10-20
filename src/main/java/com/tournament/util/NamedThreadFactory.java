package com.tournament.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Фабрика потоков с осмысленными именами.
 * <p>
 * Имена вида {@code match-worker-1}, {@code match-worker-2} позволяют
 * увидеть в логе, что коллекция действительно обрабатывается несколькими
 * потоками.
 */
public class NamedThreadFactory implements ThreadFactory {

    private final String prefix;
    private final AtomicInteger threadCounter = new AtomicInteger(1);

    public NamedThreadFactory(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            throw new IllegalArgumentException("Префикс имени потока не может быть пустым");
        }
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, prefix + threadCounter.getAndIncrement());
        thread.setDaemon(false);
        return thread;
    }
}
