package ru.bdayloop.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class DataSourceFactory {
    private DataSourceFactory() {};

    public static HikariDataSource fromEnv(){
        String url = "jdbc:postgresql://localhost:5433/" + requireEnv("POSTGRES_DB");
        return create(url, requireEnv("POSTGRES_USER"), requireEnv("POSTGRES_PASSWORD"));
    }

    private static String requireEnv(String name){
        String value =System.getenv(name);
        if(value ==null || value.isBlank()) {
            throw new IllegalArgumentException("Не задана переменная окружения" + name);
        }
        return value;
    }
    public static HikariDataSource create (String url, String user, String password){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(5_000);
        config.setPoolName("bdayloop-pool");
        return new HikariDataSource(config);
    }
}
