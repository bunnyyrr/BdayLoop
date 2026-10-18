package ru.bdayloop.web;

import ru.bdayloop.model.User;

public record AuthUser(int id, User.Role role) {
}
