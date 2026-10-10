package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.bdayloop.config.AppConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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

    @BeforeEach
    void cleanTables() throws SQLException{
        try(Connection conn = dataSource.getConnection();
            Statement st = conn.createStatement()){
            st.execute("TRUNCATE TABLE users CASCADE");
        }
    }
}
