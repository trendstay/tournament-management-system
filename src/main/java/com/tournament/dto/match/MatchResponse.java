package com.tournament.dto.match;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
/**
 * DTO ответа REST API с данными матча.
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Данные матча в ответе REST API")
public class MatchResponse {
    @Schema(description = "Идентификатор матча", example = "1")
    private Long id;
    @Schema(description = "Идентификатор команды хозяев", example = "1")
    private Long homeTeamId;
    @Schema(description = "Название команды хозяев", example = "Зенит")
    private String homeTeamName;
    @Schema(description = "Идентификатор команды гостей", example = "2")
    private Long awayTeamId;
    @Schema(description = "Название команды гостей", example = "Спартак")
    private String awayTeamName;
    @Schema(description = "Идентификатор турнира", example = "1")
    private Long tournamentId;
    @Schema(description = "Название турнира", example = "Осенний Кубок")
    private String tournamentTitle;
    @Schema(description = "Счёт матча", example = "3:1")
    private String score;
    @Schema(description = "Статус матча", example = "FINISHED")
    private String status;
    @Schema(description = "Дата и время матча", example = "2026-11-05T18:00:00")
    private LocalDateTime matchDate;
    @Schema(description = "Название команды-победителя", example = "Зенит")
    private String winnerTeamName;
    @Schema(description = "Текстовый протокол матча")
    private String matchReport;
}
