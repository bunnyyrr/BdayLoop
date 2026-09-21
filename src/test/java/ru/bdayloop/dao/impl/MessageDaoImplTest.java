package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.bdayloop.db.ConnectionManager;
import ru.bdayloop.model.Message;
import ru.bdayloop.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class MessageDaoImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withInitScript("schema.sql");

    MessageDaoImpl messageDao = new MessageDaoImpl();
    UserDaoImpl userDao = new UserDaoImpl();

    @BeforeAll
    static void setUpConnection() {
        ConnectionManager.configure(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    }

    @BeforeEach
    void cleanTables() throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             Statement st = conn.createStatement()) {
            st.execute("TRUNCATE TABLE users CASCADE");
        }
    }

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