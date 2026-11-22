package com.tournament.dto.team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
/**
 * DTO ответа REST API с данными команды.
 */
@Getter
@Setter
@NoArgsConstructor
public class TeamResponse {
    private Long id;
    private String name;
    private String city;
    private Long coachId;
    private String coachFullName;
    private Long captainId;
    private String captainFullName;
    private int playersCount;
    private List<Long> playerIds = new ArrayList<>();
    private List<Long> tournamentIds = new ArrayList<>();
}
