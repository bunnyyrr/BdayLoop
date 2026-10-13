package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.bdayloop.config.AppConfig;
import ru.bdayloop.model.User;
import ru.bdayloop.repository.i.UserRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

@SpringJUnitConfig(AppConfig.class)
abstract class DaoTestBase {
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    static{
        postgres.start();
    }

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry){
        registry.add("db.url", postgres::getJdbcUrl);
        registry.add("db.user", postgres::getUsername);
        registry.add("db.password", postgres::getPassword);
    }

    @Autowired
    DataSource dataSource;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PlatformTransactionManager transactionManager;

    @BeforeEach
    void cleanTables() throws SQLException{
        try(Connection conn = dataSource.getConnection();
            Statement st = conn.createStatement()){
            st.execute("TRUNCATE TABLE users CASCADE");
        }
    }

    User createUser(String username){
        return new TransactionTemplate(transactionManager).execute(status -> userRepository.save(new User(null, username, LocalDate.of(200, 1, 1), username, "hash", User.Role.USER)));
    }
}
