package ru.bdayloop.web.dto.response;

import ru.bdayloop.model.Message;

import java.time.LocalDateTime;

public record MessageResponse(int id, int subjectUserId, Integer senderId, String senderName, String text, LocalDateTime createdAt) {
    public static MessageResponse from(Message message) {
        Integer senderId = message.getSender() == null ? null : message.getSender().getId();
        String senderName= message.getSender() == null ? null : message.getSender().getName();
        return new MessageResponse(message.getId(), message.getSubject().getId(), senderId, senderName, message.getText(), message.getCreatedAt());
    }
}