package com.tournament.dto.team;
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
public class CaptainRequest {
    @NotNull(message = "идентификатор игрока обязателен")
    private Long playerId;
}
