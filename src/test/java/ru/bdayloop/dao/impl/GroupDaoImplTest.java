package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.bdayloop.db.ConnectionManager;
import ru.bdayloop.model.Group;
import ru.bdayloop.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class GroupDaoImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withInitScript("schema.sql");

    GroupDaoImpl groupDao = new GroupDaoImpl();
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
    void createAndFindById_returnsSameGroup() throws SQLException {
        Group created = groupDao.create(new Group(0, "Тестовая группа", null));

        Optional<Group> found = groupDao.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("Тестовая группа", found.get().getName());
    }

    @Test
    void findByMember_returnsGroupUserJoined() throws SQLException {
        User user = userDao.create(new User(0, "Катя", LocalDate.of(2000, 1, 1), "kate", "hash", User.Role.USER));
        Group group = groupDao.create(new Group(0, "Клуб", null));

        groupDao.joinGroup(user.getId(), group.getId());

        List<Group> groups = groupDao.findByMember(user.getId());
        assertEquals(1, groups.size());
        assertEquals("Клуб", groups.get(0).getName());
    }
}