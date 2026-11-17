package com.tournament.mapper;
import com.tournament.dto.user.UserResponse;
import com.tournament.model.user.PlayerUser;
import com.tournament.model.user.User;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
/**
 * Маппер пользователей: сущность {@link User} → {@link UserResponse}.
 * Отдельный класс (SRP): сервисы не занимаются преобразованием данных.
 */
@Component
public class UserMapper implements DtoMapper<User, UserResponse> {
    @Override
    public UserResponse toResponse(User entity) {
        UserResponse response = new UserResponse();
        response.setId(entity.getId());
        response.setEmail(entity.getEmail());
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setRole(entity.getRole());
        if (entity instanceof PlayerUser) {
            PlayerUser player = (PlayerUser) entity;
            response.setPhoneNumber(player.getPhoneNumber());
            response.setCaptain(player.isCaptain());
            if (player.getTeam() != null) {
                response.setTeamId(player.getTeam().getId());
            }
        }
        return response;
    }
    /** Преобразовать список сущностей в список DTO. */
    public List<UserResponse> toResponseList(List<User> entities) {
        List<UserResponse> result = new ArrayList<>();
        for (User entity : entities) {
            result.add(toResponse(entity));
        }
        return result;
    }
}
