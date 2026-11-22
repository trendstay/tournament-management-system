package com.tournament.dto.user;
import com.tournament.model.enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * DTO ответа REST API с данными пользователя.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private String phoneNumber;
    private boolean captain;
    private Long teamId;
}
