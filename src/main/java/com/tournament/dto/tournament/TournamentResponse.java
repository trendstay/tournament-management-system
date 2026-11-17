package com.tournament.dto.tournament;
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
public class TournamentResponse {
    private Long id;
    private String title;
    private String sportType;
    private String sportDisplayName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal prizePool;
    private Long refereeId;
    private String refereeFullName;
    private boolean registrationOpen;
    private int teamsCount;
    private int matchesCount;
    private List<Long> teamIds = new ArrayList<>();
    /** Текстовая турнирная таблица (формируется моделью Tournament). */
    private String standingsTable;
}
