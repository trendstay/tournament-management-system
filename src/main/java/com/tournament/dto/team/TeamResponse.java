package com.tournament.dto.team;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
/**
 * DTO ответа REST API с данными команды.
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Данные команды в ответе REST API")
public class TeamResponse {
    @Schema(description = "Идентификатор команды", example = "1")
    private Long id;
    @Schema(description = "Название команды", example = "Зенит")
    private String name;
    @Schema(description = "Город команды", example = "Санкт-Петербург")
    private String city;
    @Schema(description = "Идентификатор тренера", example = "3")
    private Long coachId;
    @Schema(description = "Имя и фамилия тренера", example = "Сергей Тренеров")
    private String coachFullName;
    @Schema(description = "Идентификатор капитана", example = "5")
    private Long captainId;
    @Schema(description = "Имя и фамилия капитана", example = "Алексей Игроков")
    private String captainFullName;
    @Schema(description = "Количество игроков в составе", example = "1")
    private int playersCount;
    @Schema(description = "Идентификаторы игроков состава", example = "[5]")
    private List<Long> playerIds = new ArrayList<>();
    @Schema(description = "Идентификаторы турниров, где участвует команда", example = "[1]")
    private List<Long> tournamentIds = new ArrayList<>();
}
