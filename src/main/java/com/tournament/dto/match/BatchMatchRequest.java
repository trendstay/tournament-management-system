package com.tournament.dto.match;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Список матчей для пакетной обработки")
public class BatchMatchRequest {
    @NotEmpty(message = "список идентификаторов матчей не может быть пустым")
    @Schema(description = "Идентификаторы матчей, которые нужно обработать", example = "[1, 2, 3]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> matchIds;
}
