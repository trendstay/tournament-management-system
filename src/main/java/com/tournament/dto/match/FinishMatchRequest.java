package com.tournament.dto.match;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO запроса на завершение матча (внесение итогового счёта).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Итоговый счёт для завершения матча")
public class FinishMatchRequest {
    @NotBlank(message = "счёт обязателен")
    @Schema(description = "Итоговый счёт в формате X:Y", example = "3:1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String score;
}
