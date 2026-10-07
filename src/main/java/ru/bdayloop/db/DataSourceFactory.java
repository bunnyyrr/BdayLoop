package ru.bdayloop.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class DataSourceFactory {
    private DataSourceFactory() {};
    
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
