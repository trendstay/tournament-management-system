package com.tournament.dto.team;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Фильтр для выборки команд из базы данных.
 * Пример: {@code GET /api/teams?city=Москва}
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Фильтр выборки команд")
public class TeamFilter {
    @Schema(description = "Город команды", example = "Москва")
    private String city;
    @Schema(description = "Часть названия команды без учёта регистра", example = "Спартак")
    private String name;
    @Schema(description = "Идентификатор тренера", example = "3")
    private Long coachId;
    public boolean isEmpty() {
        boolean noCity = city == null || city.isBlank();
        boolean noName = name == null || name.isBlank();
        return noCity && noName && coachId == null;
    }
}
