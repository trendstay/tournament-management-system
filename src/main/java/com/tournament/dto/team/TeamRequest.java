package com.tournament.dto.team;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Данные команды для создания или изменения")
public class TeamRequest {
    @NotBlank(message = "название команды обязательно")
    @Size(max = 100, message = "название не должно превышать 100 символов")
    @Schema(description = "Название команды, уникальное без учёта регистра", example = "Зенит",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    private String name;
    @Size(max = 100, message = "город не должен превышать 100 символов")
    @Schema(description = "Город команды", example = "Санкт-Петербург", maxLength = 100)
    private String city;
    /** Идентификатор тренера (пользователь с ролью COACH); может отсутствовать. */
    @Schema(description = "Идентификатор тренера (пользователь с ролью COACH)", example = "3")
    private Long coachId;
}
