package com.tournament.dto.match;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
/**
 * DTO запроса на создание/обновление матча.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequest {
    @NotNull(message = "команда хозяев обязательна")
    private Long homeTeamId;
    @NotNull(message = "команда гостей обязательна")
    private Long awayTeamId;
    private Long tournamentId;
    private LocalDateTime matchDate;
    /** Счёт в формате "X:Y"; если не задан, используется "0:0". */
    private String score;
}
