package com.tournament.dto.tournament;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO запроса на регистрацию команды в турнире.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Команда, которую регистрируют в турнире")
public class RegisterTeamRequest {
    @NotNull(message = "идентификатор команды обязателен")
    @Schema(description = "Идентификатор команды", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long teamId;
}
