package org.application.spring.configuration.jpa;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.application.spring.configuration.properties.Properties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

@Configuration("mariaHibernateConfig")
@EnableTransactionManagement
@DependsOn({"properties"})
@EnableJpaRepositories(
        basePackages = {"org.application.spring.ddd.repository"},
        transactionManagerRef = "appTM",
        entityManagerFactoryRef = "mariaDBEntityManagerFactory"
)
public class MariaHibernateConfig {

    public static void createDbIfNotExists(String dbUrl, String dbUsername, String dbPassword) {
// ابتدا بدون مشخص کردن نام دیتابیس متصل شوید تا دیتابیس ایجاد شود
        HikariConfig initialConfig = new HikariConfig();
        String dbName = dbUrl.substring(dbUrl.lastIndexOf('/') + 1);
        dbUrl = dbUrl.substring(0, dbUrl.lastIndexOf('/'));
        initialConfig.setJdbcUrl(dbUrl);
        initialConfig.setUsername(dbUsername);
        initialConfig.setPassword(dbPassword);

        try (HikariDataSource ds = new HikariDataSource(initialConfig);
             Connection connection = ds.getConnection();
             Statement statement = connection.createStatement()) {
            String sql = MessageFormat.format("CREATE DATABASE IF NOT EXISTS {0}", dbName);
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database", e);
        }
    }

    @Bean("mariaDB")
    public DataSource dataSource() {

        createDbIfNotExists(
                Properties.getSpringDatasourceUrl(),
                Properties.getSpringDatasourceUsername(),
                Properties.getSpringDatasourcePassword()
        );
        //DriverManagerDataSource ds = new DriverManagerDataSource();
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(Properties.getSpringDatasourceDriverClassName());
        config.setJdbcUrl(Properties.getSpringDatasourceUrl());
        config.setUsername(Properties.getSpringDatasourceUsername());
        config.setPassword(Properties.getSpringDatasourcePassword());
        // تنظیمات اختیاری برای performance
        config.setMaximumPoolSize(Properties.getMariadbMaximumPoolSize());
        config.setMinimumIdle(Properties.getMariadbMinimumIdle());// حداقل تعداد connectionهایی که همیشه باید idle (آماده به کار) باشن. اگر کمتر بشه، Hikari خودش connection جدید می‌سازه تا به این حد برسه.
        config.setIdleTimeout(Properties.getMariadbIdleTimeout());//مدت زمانی (به میلی‌ثانیه) که یک connection می‌تونه بدون استفاده باقی بمونه قبل از اینکه از pool حذف بشه. اینجا یعنی 30 ثانیه.
        config.setConnectionTimeout(Properties.getMariadbConnectionTimeout());// مدت زمانی که Hikari منتظر می‌مونه تا یک connection آزاد بشه. اگر در این زمان connectionی پیدا نشه، exception پرتاب می‌شه. اینجا یعنی 20 ثانیه.
        config.setPoolName(Properties.getMariadbPoolName());// اسم دلخواه برای pool. این اسم در لاگ‌ها و مانیتورینگ ظاهر می‌شه و برای دیباگ خیلی مفیده.

        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("mariaDB") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean("mariaDBEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("mariaDB") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("org.application.spring.ddd.model"); // مسیر entityها

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);

        java.util.Properties props = new java.util.Properties();
        props.setProperty("hibernate.hbm2ddl.auto", Properties.getSpringJpaHibernateDdlAuto());
        props.setProperty("hibernate.dialect", Properties.getSpringJpaPropertiesHibernateDialect());
        props.setProperty("hibernate.show_sql", String.valueOf(Properties.getSpringJpaShowSql()));
        emf.setJpaProperties(props);

        return emf;
    }

    @Bean(name = {"appTM", "transactionManager"})
    public PlatformTransactionManager transactionManager(@Qualifier("mariaDBEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

}