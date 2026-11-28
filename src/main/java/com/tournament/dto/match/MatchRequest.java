package com.tournament.dto.match;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Данные матча для создания или изменения")
public class MatchRequest {
    @NotNull(message = "команда хозяев обязательна")
    @Schema(description = "Идентификатор команды хозяев", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long homeTeamId;
    @NotNull(message = "команда гостей обязательна")
    @Schema(description = "Идентификатор команды гостей, не совпадает с командой хозяев", example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long awayTeamId;
    @Schema(description = "Идентификатор турнира (необязательно)", example = "1")
    private Long tournamentId;
    @Schema(description = "Дата и время матча", example = "2026-11-05T18:00:00")
    private LocalDateTime matchDate;
    /** Счёт в формате "X:Y"; если не задан, используется "0:0". */
    @Schema(description = "Счёт в формате X:Y; если не задан, используется 0:0", example = "3:1")
    private String score;
}
