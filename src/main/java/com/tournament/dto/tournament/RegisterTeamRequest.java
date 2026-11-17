package com.tournament.dto.tournament;
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
public class RegisterTeamRequest {
    @NotNull(message = "идентификатор команды обязателен")
    private Long teamId;
}
