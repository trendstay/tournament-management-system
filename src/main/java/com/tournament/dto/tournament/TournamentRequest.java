package com.tournament.dto.tournament;
import com.tournament.model.enums.SportType;
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
public class TournamentRequest {
    @NotBlank(message = "название турнира обязательно")
    private String title;
    @NotNull(message = "вид спорта обязателен")
    private SportType sportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal prizePool;
    /** Главный судья турнира (пользователь с ролью REFEREE). */
    private Long refereeId;
    /** Команды-участницы турнира. */
    private List<Long> teamIds = new ArrayList<>();
}
