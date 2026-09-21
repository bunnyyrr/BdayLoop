package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.bdayloop.db.ConnectionManager;
import ru.bdayloop.model.Gift;
import ru.bdayloop.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class GiftDaoImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withInitScript("schema.sql");

    GiftDaoImpl giftDao = new GiftDaoImpl();
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
    void createAndFindByUserId_returnsSameGift() throws SQLException {
        User user = userDao.create(new User(0, "Катя", LocalDate.of(2000, 1, 1), "kate", "hash", User.Role.USER));

        giftDao.create(new Gift(0, user.getId(), "Книга"));

        List<Gift> gifts = giftDao.findByUserId(user.getId());
        assertEquals(1, gifts.size());
        assertEquals("Книга", gifts.get(0).getTitle());
    }

    @Test
    void delete_removesGift() throws SQLException {
        User user = userDao.create(new User(0, "Катя", LocalDate.of(2000, 1, 1), "kate", "hash", User.Role.USER));
        Gift created = giftDao.create(new Gift(0, user.getId(), "Книга"));

        giftDao.delete(created.getId());

        Optional<Gift> found = giftDao.findById(created.getId());
        assertTrue(found.isEmpty());
    }
}