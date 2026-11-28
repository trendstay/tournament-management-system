package com.tournament.service.impl;
import com.tournament.exception.NotFoundException;
import com.tournament.mapper.DtoMapper;
import com.tournament.service.CrudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
/**
 * Абстрактная базовая реализация CRUD-операций для всех сервисов.
 *
 * <p>Шаблонный метод (Template Method): общий сценарий «найти → проверить → сохранить»
 * реализован здесь один раз, а различия выносятся в абстрактные методы
 * {@link #createEntity(Object)}, {@link #applyRequest(Object, Object)} и {@link #entityName()}.
 * Поэтому конкретные сервисы (UserServiceImpl, TeamServiceImpl и т.д.) содержат только
 * свою бизнес-логику.</p>
 *
 * @param <E>   тип сущности JPA
 * @param <REQ> тип DTO запроса
 * @param <RES> тип DTO ответа
 */
public abstract class AbstractCrudService<E, REQ, RES> implements CrudService<REQ, RES> {
    private static final Logger log = LoggerFactory.getLogger(AbstractCrudService.class);
    protected final JpaRepository<E, Long> repository;
    private final DtoMapper<E, RES> mapper;
    protected AbstractCrudService(JpaRepository<E, Long> repository, DtoMapper<E, RES> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    @Override
    @Transactional
    public RES create(REQ request) {
        E entity = createEntity(request);
        E saved = repository.save(entity);
        log.info("Создана запись: {}", entityName());
        return mapper.toResponse(saved);
    }
    @Override
    @Transactional
    public RES update(Long id, REQ request) {
        E entity = findEntity(id);
        applyRequest(entity, request);
        E saved = repository.save(entity);
        log.info("Обновлена запись: {} (id={})", entityName(), id);
        return mapper.toResponse(saved);
    }
    @Override
    @Transactional(readOnly = true)
    public RES findById(Long id) {
        return mapper.toResponse(findEntity(id));
    }
    @Override
    @Transactional(readOnly = true)
    public List<RES> findAll() {
        return toResponseList(repository.findAll());
    }
    @Override
    @Transactional
    public void delete(Long id) {
        E entity = findEntity(id);
        repository.delete(entity);
        log.info("Удалена запись: {} (id={})", entityName(), id);
    }
    /** Найти сущность или выбросить {@link NotFoundException}, если её нет. */
    protected E findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityNotFoundMessage(id)));
    }
    /** Преобразовать список сущностей в список DTO. */
    protected List<RES> toResponseList(List<E> entities) {
        List<RES> result = new ArrayList<>();
        for (E entity : entities) {
            result.add(mapper.toResponse(entity));
        }
        return result;
    }
    protected RES toResponse(E entity) {
        return mapper.toResponse(entity);
    }
    /** Создать новую сущность по DTO запроса. */
    protected abstract E createEntity(REQ request);
    /** Применить данные DTO запроса к существующей сущности. */
    protected abstract void applyRequest(E entity, REQ request);
    /** Название сущности в единственном числе (для текстов ошибок). */
    protected abstract String entityName();
    protected String entityNotFoundMessage(Long id) {
        return entityName() + " с ID " + id + " не найден(а)";
    }

}
