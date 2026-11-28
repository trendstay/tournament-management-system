package com.tournament.dto.team;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO запроса на назначение капитана команды.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Игрок, которого назначают капитаном команды")
public class CaptainRequest {
    @NotNull(message = "идентификатор игрока обязателен")
    @Schema(description = "Идентификатор игрока команды", example = "5",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long playerId;
}
