package com.tournament.model.match;

import com.tournament.model.enums.MatchStatus;
import java.time.LocalDateTime;

public class Match {
    private Long id;
    private String homeTeamName;
    private String awayTeamName;
    private String score;
    private MatchStatus status;
    private LocalDateTime matchDate;

    public Match() {
        this.status = MatchStatus.SCHEDULED;
        this.score = "0:0";
    }

    public Match(Long id, String homeTeamName, String awayTeamName, LocalDateTime matchDate) {
        this();
        this.id = id;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.matchDate = matchDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getHomeTeamName() { return homeTeamName; }
    public void setHomeTeamName(String homeTeamName) { this.homeTeamName = homeTeamName; }
    public String getAwayTeamName() { return awayTeamName; }
    public void setAwayTeamName(String awayTeamName) { this.awayTeamName = awayTeamName; }
    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }
    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }
    public LocalDateTime getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }
}