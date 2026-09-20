package ru.bdayloop.web.dto;

import java.time.LocalDate;

public record ImportUserRequest(String name, LocalDate birthday, String username, String password, String role) {
}
