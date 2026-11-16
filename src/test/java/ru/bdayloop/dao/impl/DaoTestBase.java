package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.bdayloop.model.Group;
import ru.bdayloop.model.User;
import ru.bdayloop.repository.i.GroupRepository;
import ru.bdayloop.repository.i.UserRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

@SpringBootTest
abstract class DaoTestBase {
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    static{
        postgres.start();
    }

    @Autowired
    DataSource dataSource;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    PlatformTransactionManager transactionManager;

    @BeforeEach
    void cleanTables() throws SQLException {
        try(Connection conn = dataSource.getConnection();
            Statement st = conn.createStatement()){
            st.execute("TRUNCATE TABLE users CASCADE");
        }
    }

    User createUser(String username){
        return new TransactionTemplate(transactionManager).execute(status -> userRepository.save(new User(null, username, LocalDate.of(2000, 1, 1), username, "hash", User.Role.USER)));
    }

    Group createGroup(String name){
        return new TransactionTemplate(transactionManager).execute(status -> groupRepository.save(new Group(name, null)));
    }
}
