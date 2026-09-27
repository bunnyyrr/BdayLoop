package ru.bdayloop.web.dto.request;

public record UpdateUserRequest(String name, java.time.LocalDate birthday, String username) {}
