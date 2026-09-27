package ru.bdayloop.service.i;

import ru.bdayloop.model.Message;

import java.sql.SQLException;
import java.util.List;

public interface MessageService {
    Message sendMessage(Message message) throws SQLException;
    List<Message> getMessages(int subjectUserId, int senderId) throws SQLException;
}
