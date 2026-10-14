package ru.bdayloop.service.i;

import ru.bdayloop.model.Message;

import java.sql.SQLException;
import java.util.List;

public interface MessageService {
    Message sendMessage(int subjectId, int senderId, String text);
    List<Message> getMessages(int subjectUserId, int senderId);
}
