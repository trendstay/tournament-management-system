package com.tournament.service;
import java.util.List;
/**
 * Общий контракт CRUD-операций над сущностью (принцип единственной ответственности:
 * каждый сервис отвечает только за «свою» сущность).
 *
 * @param <REQ> тип DTO запроса
 * @param <RES> тип DTO ответа
 */
public interface CrudService<REQ, RES> {
    /** Создание объекта (CREATE). */
    RES create(REQ request);
    /** Обновление объекта по идентификатору (UPDATE). */
    RES update(Long id, REQ request);
    /** Получение объекта по идентификатору (READ). */
    RES findById(Long id);
    /** Получение всех объектов (READ). */
    List<RES> findAll();
    /** Удаление объекта по идентификатору (DELETE). */
    void delete(Long id);
}
