package ru.bdayloop.service.command;

import ru.bdayloop.model.User;

public record UserRegistration(User user, String password) { }
