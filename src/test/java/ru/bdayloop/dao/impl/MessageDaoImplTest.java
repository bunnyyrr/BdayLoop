package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bdayloop.dao.i.MessageDao;
import ru.bdayloop.model.Message;
import ru.bdayloop.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageDaoImplTest extends DaoTestBase {

    @Autowired
    MessageDao messageDao;

    @Test
    void createAndFindBySubjectUserId_returnsSameMessage() throws SQLException {
        User subject = createUser("subject");
        User sender = createUser("sender");

        messageDao.create(new Message(0, subject.getId(), sender.getId(), "привет", null));

        List<Message> messages = messageDao.findBySubjectUserId(subject.getId());
        assertEquals(1, messages.size());
        assertEquals("привет", messages.get(0).getText());
    }
}