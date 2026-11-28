package com.tournament.dto.user;
import com.tournament.model.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO запроса на создание/обновление пользователя (используется во всех CRUD-методах).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные пользователя для создания или изменения учётной записи")
public class UserRequest {
    @NotBlank(message = "email обязателен")
    @Email(message = "некорректный формат email")
    @Schema(description = "Email, он же имя входа в систему", example = "coach@tournament.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    /** Пароль в открытом виде. При создании обязателен, при обновлении можно не передавать. */
    @Size(min = 6, max = 100, message = "пароль должен быть от 6 до 100 символов")
    @Schema(description = "Пароль от 6 до 100 символов. Хранится в виде BCrypt-хеша. "
            + "При обновлении можно не передавать — тогда пароль не меняется",
            example = "pass123", minLength = 6, maxLength = 100)
    private String password;
    @NotBlank(message = "имя обязательно")
    @Schema(description = "Имя", example = "Сергей", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;
    @NotBlank(message = "фамилия обязательна")
    @Schema(description = "Фамилия", example = "Тренеров", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;
    @NotNull(message = "роль обязательна")
    @Schema(description = "Роль пользователя в системе", example = "COACH",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UserRole role;
    /** Только для роли PLAYER/CAPTAIN. */
    @Schema(description = "Телефон (только для роли PLAYER или CAPTAIN)", example = "+79991234567")
    private String phoneNumber;
    /** Только для роли PLAYER/CAPTAIN: идентификатор команды. */
    @Schema(description = "Идентификатор команды (только для роли PLAYER или CAPTAIN)", example = "1")
    private Long teamId;
}
