package ru.bdayloop.dao;

import ru.bdayloop.model.Message;

import java.sql.SQLException;
import java.util.List;

public interface MessageDao {
    Message create(Message message) throws SQLException;
    List<Message> findBySubjectUserId(int subjectUserId) throws SQLException;
}
