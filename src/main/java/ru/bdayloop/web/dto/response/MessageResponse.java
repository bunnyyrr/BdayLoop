package ru.bdayloop.web.dto.response;

import ru.bdayloop.model.Message;

import java.time.LocalDateTime;

public record MessageResponse(int id, int subjectUserId, int senderId, String text, LocalDateTime createdAt) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(message.getId(), message.getSubjectUserId(), message.getSenderId(), message.getText(), message.getCreatedAt());
    }
}
