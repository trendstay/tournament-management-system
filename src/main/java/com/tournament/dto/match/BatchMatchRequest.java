package com.tournament.dto.match;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
/**
 * DTO запроса на завершение счёта матча и пакетной (многопоточной) обработки матчей.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchMatchRequest {
    @NotEmpty(message = "список идентификаторов матчей не может быть пустым")
    private List<Long> matchIds;
}
