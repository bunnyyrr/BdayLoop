package ru.bdayloop.web.dto.request;

import java.time.LocalDate;

public record RegisterRequest (String name, LocalDate birthday, String username, String password){}
