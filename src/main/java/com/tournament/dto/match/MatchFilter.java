package com.tournament.dto.match;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
/**
 * Фильтр для выборки матчей из базы данных.
 * Пример: {@code GET /api/matches?status=FINISHED&teamId=3}
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Фильтр выборки матчей")
public class MatchFilter {
    @Schema(description = "Статус матча", example = "FINISHED")
    private String status;
    @Schema(description = "Идентификатор турнира", example = "1")
    private Long tournamentId;
    @Schema(description = "Идентификатор команды (хозяева или гости)", example = "2")
    private Long teamId;
    @Schema(description = "Начало периода даты матча", example = "2026-01-01T00:00:00")
    private LocalDateTime from;
    @Schema(description = "Конец периода даты матча", example = "2026-12-31T23:59:59")
    private LocalDateTime to;
    public boolean isEmpty() {
        boolean noStatus = status == null || status.isBlank();
        boolean noDate = from == null && to == null;
        return noStatus && tournamentId == null && teamId == null && noDate;
    }
}
