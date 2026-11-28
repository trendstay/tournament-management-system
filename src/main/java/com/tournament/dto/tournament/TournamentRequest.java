package com.tournament.dto.tournament;
import com.tournament.model.enums.SportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * DTO запроса на создание/обновление турнира.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные турнира для создания или изменения")
public class TournamentRequest {
    @NotBlank(message = "название турнира обязательно")
    @Schema(description = "Название турнира", example = "Осенний Кубок",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
    @NotNull(message = "вид спорта обязателен")
    @Schema(description = "Вид спорта турнира", example = "FOOTBALL",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private SportType sportType;
    @Schema(description = "Дата начала турнира", example = "2026-11-01")
    private LocalDate startDate;
    @Schema(description = "Дата окончания турнира, не раньше даты начала", example = "2026-11-10")
    private LocalDate endDate;
    @Schema(description = "Призовой фонд", example = "50000")
    private BigDecimal prizePool;
    /** Главный судья турнира (пользователь с ролью REFEREE). */
    @Schema(description = "Идентификатор главного судьи (пользователь с ролью REFEREE)", example = "3")
    private Long refereeId;
    /** Команды-участницы турнира. */
    @Schema(description = "Идентификаторы команд-участниц", example = "[1, 2]")
    private List<Long> teamIds = new ArrayList<>();
}
