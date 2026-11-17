package com.tournament.service;
import com.tournament.dto.tournament.TournamentFilter;
import com.tournament.dto.tournament.TournamentRequest;
import com.tournament.dto.tournament.TournamentResponse;
import java.util.List;
/**
 * Сервис управления турнирами (CRUD + регистрация команд + фильтры).
 */
public interface TournamentService extends CrudService<TournamentRequest, TournamentResponse> {
    /** Выборка турниров по фильтру (вид спорта, название, даты, судья). */
    List<TournamentResponse> findByFilter(TournamentFilter filter);
    /** Зарегистрировать команду в турнире. */
    TournamentResponse registerTeam(Long tournamentId, Long teamId);
    /** Исключить команду из турнира. */
    TournamentResponse unregisterTeam(Long tournamentId, Long teamId);
    /** Турнирная таблица в текстовом виде. */
    String getStandingsTable(Long tournamentId);
}
