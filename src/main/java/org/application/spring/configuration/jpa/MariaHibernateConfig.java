package org.application.spring.configuration.jpa;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.application.spring.configuration.Properties;
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

@Configuration("mriaHibernateConfig")
@EnableTransactionManagement
@DependsOn({"properties"})
@EnableJpaRepositories(basePackages = {
        "org.application.spring.ddd.repository"
})
public class MariaHibernateConfig {


    @Bean
    public DataSource dataSource() {
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
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
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

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

}