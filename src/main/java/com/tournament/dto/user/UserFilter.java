package com.tournament.dto.user;
import com.tournament.model.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Фильтр для выборки пользователей из базы данных.
 * Заполняется из query-параметров запроса: {@code GET /api/users?role=COACH&lastName=Ив}
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Фильтр выборки пользователей")
public class UserFilter {
    @Schema(description = "Роль пользователя", example = "COACH")
    private UserRole role;
    @Schema(description = "Часть фамилии без учёта регистра", example = "Ив")
    private String lastName;
    public boolean isEmpty() {
        boolean noRole = role == null;
        boolean noLastName = lastName == null || lastName.isBlank();
        return noRole && noLastName;
    }
}
