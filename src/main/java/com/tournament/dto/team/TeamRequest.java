package com.tournament.dto.team;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO запроса на создание/обновление команды.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequest {
    @NotBlank(message = "название команды обязательно")
    @Size(max = 100, message = "название не должно превышать 100 символов")
    private String name;
    @Size(max = 100, message = "город не должен превышать 100 символов")
    private String city;
    /** Идентификатор тренера (пользователь с ролью COACH); может отсутствовать. */
    private Long coachId;
}
