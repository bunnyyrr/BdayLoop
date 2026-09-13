package ru.bdayloop.web.dto;

public record UpdateUserRequest(String name, java.time.LocalDate birthday, String username) {}
