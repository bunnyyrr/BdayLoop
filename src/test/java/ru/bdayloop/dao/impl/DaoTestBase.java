package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.bdayloop.db.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

abstract class DaoTestBase {
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16").withInitScript("schema.sql");
    static final DataSource dataSource;

    static{
        postgres.start();
        dataSource= DataSourceFactory.create(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    }

    @BeforeEach
    void cleanTables() throws SQLException{
        try(Connection conn = dataSource.getConnection();
            Statement st = conn.createStatement()){
            st.execute("TRUNCATE TABLE users CASCADE");
        }
    }
}
