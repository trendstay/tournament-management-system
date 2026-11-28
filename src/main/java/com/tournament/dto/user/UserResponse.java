package com.tournament.dto.user;
import com.tournament.model.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO ответа REST API с данными пользователя.
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Данные пользователя в ответе REST API (пароль не возвращается)")
public class UserResponse {
    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long id;
    @Schema(description = "Email пользователя", example = "coach@tournament.com")
    private String email;
    @Schema(description = "Имя", example = "Сергей")
    private String firstName;
    @Schema(description = "Фамилия", example = "Тренеров")
    private String lastName;
    @Schema(description = "Роль пользователя", example = "COACH")
    private UserRole role;
    @Schema(description = "Телефон игрока", example = "+79991234567")
    private String phoneNumber;
    @Schema(description = "Признак капитана команды", example = "false")
    private boolean captain;
    @Schema(description = "Идентификатор команды игрока", example = "1")
    private Long teamId;
}
