package com.tournament.dto.match;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
/**
 * DTO ответа REST API с данными матча.
 */
@Getter
@Setter
@NoArgsConstructor
public class MatchResponse {
    private Long id;
    private Long homeTeamId;
    private String homeTeamName;
    private Long awayTeamId;
    private String awayTeamName;
    private Long tournamentId;
    private String tournamentTitle;
    private String score;
    private String status;
    private LocalDateTime matchDate;
    private String winnerTeamName;
    private String matchReport;
}
