package com.tournament.dto.user;
import com.tournament.model.enums.UserRole;
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
public class UserRequest {
    @NotBlank(message = "email обязателен")
    @Email(message = "некорректный формат email")
    private String email;
    /** Пароль в открытом виде. При создании обязателен, при обновлении можно не передавать. */
    @Size(min = 6, max = 100, message = "пароль должен быть от 6 до 100 символов")
    private String password;
    @NotBlank(message = "имя обязательно")
    private String firstName;
    @NotBlank(message = "фамилия обязательна")
    private String lastName;
    @NotNull(message = "роль обязательна")
    private UserRole role;
    /** Только для роли PLAYER/CAPTAIN. */
    private String phoneNumber;
    /** Только для роли PLAYER/CAPTAIN: идентификатор команды. */
    private Long teamId;
}
