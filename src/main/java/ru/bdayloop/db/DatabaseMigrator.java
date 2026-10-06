package ru.bdayloop.db;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public final class DatabaseMigrator {
    private DatabaseMigrator() {};

    public static void migrate(DataSource dataSource){
        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();
    }
}
