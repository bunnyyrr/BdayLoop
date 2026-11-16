package ru.bdayloop.web.dto.request;

public record ChangePasswordRequest(String oldPassword, String newPassword) {
}
