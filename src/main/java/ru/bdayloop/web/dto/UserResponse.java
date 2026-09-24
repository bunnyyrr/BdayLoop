package ru.bdayloop.web.dto;
import ru.bdayloop.model.User;

import java.time.LocalDate;

public record UserResponse(int id, String name, LocalDate birthday, String username, User.Role role) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getBirthday(), user.getUsername(), user.getRole());
    }
}
