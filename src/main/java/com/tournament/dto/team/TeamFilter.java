package com.tournament.dto.team;
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
public class TeamFilter {
    private String city;
    private String name;
    private Long coachId;
    public boolean isEmpty() {
        boolean noCity = city == null || city.isBlank();
        boolean noName = name == null || name.isBlank();
        return noCity && noName && coachId == null;
    }
}
