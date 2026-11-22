package com.tournament.dto.match;
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
public class MatchFilter {
    private String status;
    private Long tournamentId;
    private Long teamId;
    private LocalDateTime from;
    private LocalDateTime to;
    public boolean isEmpty() {
        boolean noStatus = status == null || status.isBlank();
        boolean noDate = from == null && to == null;
        return noStatus && tournamentId == null && teamId == null && noDate;
    }
}
