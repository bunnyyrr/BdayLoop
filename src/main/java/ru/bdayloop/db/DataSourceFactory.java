package ru.bdayloop.db;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public final class DataSourceFactory {
    private DataSourceFactory() {};

    public static DataSource fromEnv(){
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
    public static DataSource create (String url, String user, String password){
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        return dataSource;
    }
}
