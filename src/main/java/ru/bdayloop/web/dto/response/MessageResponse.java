package ru.bdayloop.web.dto.response;

import ru.bdayloop.model.Message;

import java.time.LocalDateTime;

public record MessageResponse(int id, int subjectUserId, int senderId, String text, LocalDateTime createdAt) {
    public static MessageResponse from(Message message) {
        Integer senderId= message.getSender() == null? null : message.getSender().getId();
        return new MessageResponse(message.getId(), message.getSubject().getId(), message.getSender().getId(), message.getText(), message.getCreatedAt());
    }
}
