package com.tournament.dto.user;
import com.tournament.model.enums.UserRole;
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
public class UserFilter {
    private UserRole role;
    private String lastName;
    public boolean isEmpty() {
        boolean noRole = role == null;
        boolean noLastName = lastName == null || lastName.isBlank();
        return noRole && noLastName;
    }
}
