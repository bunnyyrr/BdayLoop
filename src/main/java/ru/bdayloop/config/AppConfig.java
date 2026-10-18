package ru.bdayloop.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages= "ru.bdayloop", excludeFilters= @ComponentScan.Filter(type = FilterType.REGEX, pattern = "ru\\.bdayloop\\.web\\..*"))
@PropertySource("classpath:application.properties")
@PropertySource(value = "file:.env", ignoreResourceNotFound = true)
public class AppConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer(){
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource(@Value("${db.url}") String url,
                                       @Value("${db.user}") String user,
                                       @Value("${db.password}") String password,
                                       @Value("${db.pool-size}") int poolSize) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(poolSize);
        config.setConnectionTimeout(5000);
        config.setPoolName("bdayloop-pool");
        return new HikariDataSource(config);
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource){
        return Flyway.configure()
                .dataSource(dataSource)
                .load();
    }
}
