package com.tournament.repository;

import com.tournament.model.Identifiable;

import java.util.List;
import java.util.Optional;

/**
 * Базовый контракт хранения сущностей.
 * <p>
 * Позволяет единообразно работать с разными сущностями и переиспользовать
 * общую реализацию хранилища (принцип единственной ответственности).
 *
 * @param <T> тип хранимой сущности
 */
public interface BaseRepository<T extends Identifiable> {

    /**
     * Сохраняет сущность и назначает ей идентификатор, если он ещё не задан.
     */
    T save(T entity);

    /**
     * Находит сущность по идентификатору.
     */
    Optional<T> findById(Long id);

    /**
     * Возвращает все сущности, отсортированные по идентификатору.
     */
    List<T> findAll();

    /**
     * Удаляет сущность по идентификатору.
     *
     * @return {@code true}, если сущность существовала и была удалена
     */
    boolean deleteById(Long id);

    /**
     * Количество сохранённых сущностей.
     */
    int count();
}
