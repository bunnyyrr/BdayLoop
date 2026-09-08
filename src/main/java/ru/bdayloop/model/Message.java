package ru.bdayloop.model;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private int subjectUserId;
    private int senderId;
    private String text;
    private LocalDateTime createdAt;

    public Message(int id, int subjectUserId, int senderId, String text, LocalDateTime createdAt){
        this.id =id;
        this.subjectUserId = subjectUserId;
        this.senderId =senderId;
        this.text =text;
        this.createdAt =createdAt;
    }

    public int getId() {
        return id;
    }
    public int getSenderId() {
        return senderId;
    }
    public int getSubjectUserId() {return subjectUserId;}
    public String getText() {
        return text;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
