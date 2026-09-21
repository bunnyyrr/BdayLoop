package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.bdayloop.db.ConnectionManager;
import ru.bdayloop.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withInitScript("schema.sql");

    UserDaoImpl userDao = new UserDaoImpl();

    @BeforeAll
    static void setUpConnection() {
        ConnectionManager.configure(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    }

    @BeforeEach
    void cleanTable() throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             Statement st = conn.createStatement()) {
            st.execute("TRUNCATE TABLE users CASCADE");
        }
    }

    @Test
    void createAndFindById_returnsSameUser() throws SQLException {
        User newUser = new User(0, "Катя", LocalDate.of(2000, 1, 1), "kate", "hash123", User.Role.USER);

        User created = userDao.create(newUser);

        Optional<User> found = userDao.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("Катя", found.get().getName());
        assertEquals("kate", found.get().getUsername());
    }

    @Test
    void findById_whenUserDoesNotExist_returnsEmpty() throws SQLException {
        Optional<User> found = userDao.findById(999);
        assertTrue(found.isEmpty());
    }

    @Test
    void findByUsername_findsExistingUser() throws SQLException {
        userDao.create(new User(0, "Аня", LocalDate.of(1999, 5, 20), "anya", "hash456", User.Role.USER));

        Optional<User> found = userDao.findByUsername("anya");
        assertTrue(found.isPresent());
        assertEquals("Аня", found.get().getName());
    }
}