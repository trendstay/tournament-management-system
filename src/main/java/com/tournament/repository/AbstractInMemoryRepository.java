package com.tournament.repository;

import com.tournament.model.Identifiable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Общая реализация in-memory хранилища на {@link ConcurrentHashMap}.
 * <p>
 * Класс инкапсулирует назначение идентификаторов и потокобезопасное хранение,
 * а наследники добавляют только специфичные методы поиска. Такое разделение
 * соответствует принципу единственной ответственности.
 *
 * @param <T> тип хранимой сущности
 */
public abstract class AbstractInMemoryRepository<T extends Identifiable> implements BaseRepository<T> {

    private static final Logger LOGGER = Logger.getLogger(AbstractInMemoryRepository.class.getName());

    private final Map<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @Override
    public T save(T entity) {
        Objects.requireNonNull(entity, "Сущность не может быть null");
        if (entity.getId() == null) {
            entity.setId(idSequence.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        LOGGER.fine(() -> "Сохранена сущность " + entity.getClass().getSimpleName()
                + " с ID " + entity.getId());
        return entity;
    }

    @Override
    public Optional<T> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        List<T> entities = new ArrayList<>(storage.values());
        entities.sort(Comparator.comparing(entity -> entity.getId()));
        return entities;
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }
        return storage.remove(id) != null;
    }

    @Override
    public int count() {
        return storage.size();
    }

    /**
     * Возвращает все сущности, удовлетворяющие условию, в порядке возрастания ID.
     */
    protected List<T> filter(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "Условие поиска не может быть null");
        List<T> result = new ArrayList<>();
        for (T entity : storage.values()) {
            if (predicate.test(entity)) {
                result.add(entity);
            }
        }
        result.sort(Comparator.comparing(entity -> entity.getId()));
        return result;
    }

    /**
     * Возвращает первую сущность, удовлетворяющую условию.
     */
    protected Optional<T> findFirst(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "Условие поиска не может быть null");
        for (T entity : storage.values()) {
            if (predicate.test(entity)) {
                return Optional.of(entity);
            }
        }
        return Optional.empty();
    }
}
