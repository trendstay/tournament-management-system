package com.tournament.dto.match;
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
public class FinishMatchRequest {
    @NotBlank(message = "счёт обязателен")
    private String score;
}
