package com.tournament.service;
import com.tournament.dto.match.BatchMatchRequest;
import com.tournament.dto.match.FinishMatchRequest;
import com.tournament.dto.match.MatchFilter;
import com.tournament.dto.match.MatchRequest;
import com.tournament.dto.match.MatchResponse;
import java.util.List;
/**
 * Сервис управления матчами (CRUD + внесение результата + фильтры).
 */
public interface MatchService extends CrudService<MatchRequest, MatchResponse> {
    /** Выборка матчей по фильтру (статус, турнир, команда, период дат). */
    List<MatchResponse> findByFilter(MatchFilter filter);
    /** Завершить матч и внести итоговый счёт. */
    MatchResponse finishMatch(Long matchId, FinishMatchRequest request);
    /** Пакетная многопоточная обработка матчей (как в лабораторной работе №1). */
    List<MatchResponse> processMatchesConcurrently(BatchMatchRequest request);
}
