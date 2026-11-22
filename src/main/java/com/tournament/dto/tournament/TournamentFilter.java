package com.tournament.dto.tournament;
import com.tournament.model.enums.SportType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
/**
 * Фильтр для выборки турниров из базы данных.
 * Пример: {@code GET /api/tournaments? sportType=FOOTBALL&startFrom=2026-01-01&startTo=2026-12-31}
 */
@Getter
@Setter
@NoArgsConstructor
public class TournamentFilter {
    private SportType sportType;
    private String title;
    private LocalDate startFrom;
    private LocalDate startTo;
    private Long refereeId;
    public boolean isEmpty() {
        boolean noSport = sportType == null;
        boolean noTitle = title == null || title.isBlank();
        boolean noDates = startFrom == null && startTo == null;
        return noSport && noTitle && noDates && refereeId == null;
    }
}
