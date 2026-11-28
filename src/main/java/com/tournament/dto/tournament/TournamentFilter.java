package com.tournament.dto.tournament;
import com.tournament.model.enums.SportType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Фильтр выборки турниров")
public class TournamentFilter {
    @Schema(description = "Вид спорта", example = "FOOTBALL")
    private SportType sportType;
    @Schema(description = "Часть названия турнира без учёта регистра", example = "Кубок")
    private String title;
    @Schema(description = "Начало периода дат турнира", example = "2026-01-01")
    private LocalDate startFrom;
    @Schema(description = "Конец периода дат турнира", example = "2026-12-31")
    private LocalDate startTo;
    @Schema(description = "Идентификатор судьи", example = "3")
    private Long refereeId;
    public boolean isEmpty() {
        boolean noSport = sportType == null;
        boolean noTitle = title == null || title.isBlank();
        boolean noDates = startFrom == null && startTo == null;
        return noSport && noTitle && noDates && refereeId == null;
    }
}
