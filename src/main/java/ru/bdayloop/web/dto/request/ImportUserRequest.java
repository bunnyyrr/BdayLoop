package ru.bdayloop.web.dto.request;

import java.time.LocalDate;

public record ImportUserRequest(String name, LocalDate birthday, String username, String password, String role) {
}
