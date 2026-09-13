package ru.bdayloop.web.dto;

public record SendMessageRequest(int subjectId, int senderId, String text) {}
