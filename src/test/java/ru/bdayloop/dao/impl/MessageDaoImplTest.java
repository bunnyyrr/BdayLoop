package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.bdayloop.model.Message;
import ru.bdayloop.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageDaoImplTest extends DaoTestBase {

    MessageDaoImpl messageDao = new MessageDaoImpl(dataSource);
    UserDaoImpl userDao = new UserDaoImpl(dataSource);

    @Test
    void createAndFindBySubjectUserId_returnsSameMessage() throws SQLException {
        User subject = userDao.create(new User(0, "Именинник", LocalDate.of(2000, 1, 1), "subject", "hash", User.Role.USER));
        User sender = userDao.create(new User(0, "Отправитель", LocalDate.of(1999, 5, 20), "sender", "hash", User.Role.USER));

        messageDao.create(new Message(0, subject.getId(), sender.getId(), "привет", null));

        List<Message> messages = messageDao.findBySubjectUserId(subject.getId());
        assertEquals(1, messages.size());
        assertEquals("привет", messages.get(0).getText());
    }
}