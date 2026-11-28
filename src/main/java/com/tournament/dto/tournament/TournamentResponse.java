package com.tournament.dto.tournament;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * DTO ответа REST API с данными турнира.
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Данные турнира в ответе REST API")
public class TournamentResponse {
    @Schema(description = "Идентификатор турнира", example = "1")
    private Long id;
    @Schema(description = "Название турнира", example = "Осенний Кубок")
    private String title;
    @Schema(description = "Вид спорта", example = "FOOTBALL")
    private String sportType;
    @Schema(description = "Название вида спорта для вывода", example = "Футбол")
    private String sportDisplayName;
    @Schema(description = "Дата начала", example = "2026-11-01")
    private LocalDate startDate;
    @Schema(description = "Дата окончания", example = "2026-11-10")
    private LocalDate endDate;
    @Schema(description = "Призовой фонд", example = "50000")
    private BigDecimal prizePool;
    @Schema(description = "Идентификатор судьи", example = "3")
    private Long refereeId;
    @Schema(description = "Имя и фамилия судьи", example = "Николай Судьин")
    private String refereeFullName;
    @Schema(description = "Открыта ли регистрация команд", example = "true")
    private boolean registrationOpen;
    @Schema(description = "Количество команд-участниц", example = "2")
    private int teamsCount;
    @Schema(description = "Количество матчей турнира", example = "1")
    private int matchesCount;
    @Schema(description = "Идентификаторы команд-участниц", example = "[1, 2]")
    private List<Long> teamIds = new ArrayList<>();
    /** Текстовая турнирная таблица (формируется моделью Tournament). */
    @Schema(description = "Текстовая турнирная таблица (формируется моделью Tournament)")
    private String standingsTable;
}
