package ru.bdayloop.web.dto;

import java.time.LocalDate;

public record RegisterRequest (String name, LocalDate birthday, String username, String password){}
