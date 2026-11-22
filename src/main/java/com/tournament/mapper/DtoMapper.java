package com.tournament.mapper;
/**
 * Общий интерфейс преобразования сущности базы данных в DTO ответа.
 *
 * @param <E>   тип сущности JPA
 * @param <RES> тип DTO ответа
 */
public interface DtoMapper<E, RES> {
    /** Преобразовать сущность в DTO. */
    RES toResponse(E entity);
}
