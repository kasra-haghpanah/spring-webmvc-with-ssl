package org.application.spring.configuration.jpa;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.application.spring.configuration.properties.Properties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@DependsOn({"mariaHibernateConfig"})
public class DatabaseHealthChecker {

    private final DataSource dataSource;
    private final RetryTemplate retryTemplate;
    // if corePoolSize gets 4, you can use 4 schedule with this scheduler.
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, Thread.ofVirtual().name("scheduler-", 0).factory());


    public DatabaseHealthChecker(DataSource dataSource) {
        this.dataSource = dataSource;
        this.retryTemplate = new RetryTemplate();
        // تنظیم retry policy برای تلاش نامحدود تا زمانی که موفق شود
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(Integer.MAX_VALUE);
        retryTemplate.setRetryPolicy(retryPolicy);
        // تنظیم backoff policy برای 30 ثانیه تأخیر بین هر تلاش
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(Properties.getDatabaseHealthCheckerBackoffPeriodInSecond()); // 30 ثانیه
        retryTemplate.setBackOffPolicy(backOffPolicy);
    }

    @PostConstruct
    public void start() {
        scheduler.scheduleAtFixedRate(this::checkDatabaseConnection, 0, Properties.getDatabaseHealthCheckerSchedulePeriodInSecond(), TimeUnit.SECONDS);
    }

    @PreDestroy
    public void end(){
        scheduler.shutdown();
    }

    // هر 1 دقیقه یکبار دیتابیس را چک می‌کنیم
    //@Scheduled(fixedRate = 60_000)
    public void checkDatabaseConnection() {
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("SELECT 1").execute();
            System.out.println("✅ Database is UP");
        } catch (SQLException e) {
            System.err.println("❌ Database is DOWN. Trying to reconnect...");

            retryTemplate.execute((RetryCallback<Void, RuntimeException>) context -> {
                try (Connection conn = dataSource.getConnection()) {
                    conn.prepareStatement("SELECT 1").execute();
                    System.out.println("✅ Database reconnected successfully!");
                    return null;
                } catch (SQLException ex) {
                    System.err.println("❌ Retry failed. Attempt #" + (context.getRetryCount() + 1));
                    throw new RuntimeException(ex);
                }
            });
        }
    }
}
