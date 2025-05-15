package ru.yandex.blog.configuration;

import org.h2.Driver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:secret.properties")
@PropertySource("classpath:application.properties")
public class DataSourceConfiguration {

    @Value("${project-path}")
    private String projectPath;

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password
    ) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Driver.class.getName());
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @EventListener
    public void populate(ContextRefreshedEvent event) {
        DataSource dataSource = event.getApplicationContext().getBean(DataSource.class);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql")); // Файл должен находиться в ресурсах
        populator.execute(dataSource);

        JdbcTemplate jdbcTemplate = event.getApplicationContext().getBean(JdbcTemplate.class);

        jdbcTemplate.execute("insert into images(post_id, image) values (1, file_read('" + projectPath + "src/main/resources/assets/1.jpg'));");
        jdbcTemplate.execute("insert into images(post_id, image) values (2, file_read('" + projectPath + "src/main/resources/assets/2.jpg'));");
        jdbcTemplate.execute("insert into images(post_id, image) values (3, file_read('" + projectPath + "src/main/resources/assets/3.jpg'));");
    }

}
