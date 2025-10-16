package org.application.spring.configuration.properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.application.spring.configuration.restclient.RestClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration("properties")
public class Properties {

    public static String Localhost = "localhost";
    private static final Map<String, Object> config = new HashMap<String, Object>();

    public Properties(Environment environment) {

        String classPath = RestClientConfig.class.getResource("").getPath();
        classPath = classPath.substring(0, classPath.indexOf("/classes") + 8);

        config.put("classpath", classPath);
        config.put("version", environment.getProperty("version"));
        config.put("jackson.time-zone", environment.getProperty("jackson.time-zone"));
        config.put("cookie.age.minutes", environment.getProperty("cookie.age.minutes"));
        config.put("server.port", environment.getProperty("server.port"));
        config.put("spring.application.name", environment.getProperty("spring.application.name"));
        config.put("application.upload_file_size_in_mb", environment.getProperty("application.upload_file_size_in_mb"));
        config.put("springdoc.api-docs.version", environment.getProperty("springdoc.api-docs.version"));
        config.put("springdoc.packagesToScan", environment.getProperty("springdoc.packagesToScan"));
        config.put("app.jwt-secret", environment.getProperty("app.jwt-secret"));
        config.put("app.jwt-expiration-minutes", environment.getProperty("app.jwt-expiration-minutes"));
        config.put("spring.datasource.url", environment.getProperty("spring.datasource.url"));
        config.put("spring.datasource.username", environment.getProperty("spring.datasource.username"));
        config.put("spring.datasource.password", environment.getProperty("spring.datasource.password"));
        config.put("spring.datasource.driver-class-name", environment.getProperty("spring.datasource.driver-class-name"));
        config.put("spring.jpa.properties.hibernate.dialect", environment.getProperty("spring.jpa.properties.hibernate.dialect"));
        config.put("spring.jpa.show-sql", environment.getProperty("spring.jpa.show-sql"));
        config.put("spring.jpa.hibernate.ddl-auto", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        config.put("mariadb.maximum-pool-size", environment.getProperty("mariadb.maximum-pool-size"));
        config.put("mariadb.minimum-idle", environment.getProperty("mariadb.minimum-idle"));
        config.put("mariadb.idleTimeout", environment.getProperty("mariadb.idleTimeout"));
        config.put("mariadb.connection-timeout", environment.getProperty("mariadb.connection-timeout"));
        config.put("mariadb.pool-name", environment.getProperty("mariadb.pool-name"));
        config.put("cors.allowed-origins", environment.getProperty("cors.allowed-origins"));
        config.put("thymeleaf.cacheable", environment.getProperty("thymeleaf.cacheable"));
        config.put("email.username", environment.getProperty("email.username"));
        config.put("email.password", environment.getProperty("email.password"));
        config.put("email.base-url", environment.getProperty("email.base-url"));
        config.put("database-health-checker.schedule.period-in-second", environment.getProperty("database-health-checker.schedule.period-in-second"));
        config.put("database-health-checker.backoff.period-in-second", environment.getProperty("database-health-checker.backoff.period-in-second"));
        config.put("log.path", environment.getProperty("log.path"));
        config.put("log.time-zone", environment.getProperty("log.time-zone"));
        config.put("log.max-file-size", environment.getProperty("log.max-file-size"));
        config.put("log.archive.file-name-pattern", environment.getProperty("log.archive.file-name-pattern"));
        config.put("log.archive.max-number-file", environment.getProperty("log.archive.max-number-file"));
        config.put("log.archive.total-size-cap", environment.getProperty("log.archive.total-size-cap"));
        config.put("log.console.active", environment.getProperty("log.console.active"));

        config.put("limit-rating.capacity", environment.getProperty("limit-rating.capacity"));
        config.put("limit-rating.refill-tokens", environment.getProperty("limit-rating.refill-tokens"));
        config.put("limit-rating.refill-duration-in-second", environment.getProperty("limit-rating.refill-duration-in-second"));
        config.put("limit-rating.list", environment.getProperty("limit-rating.list"));

    }

    private static <T> T get(String key, Class<T> T) {
        return (T) config.get(key);
    }

    public static String getClassPath() {
        return get("classpath", String.class);
    }

    public static Integer getServerPort() {
        return Integer.valueOf(get("server.port", String.class));
    }

    public static String getApplicationName() {
        return get("spring.application.name", String.class);
    }

    public static Integer getApplicationUploadFileSizeInMB() {
        return Integer.valueOf(get("application.upload_file_size_in_mb", String.class));
    }

    public static String getSpringdocApidocsVersion() {
        return get("springdoc.api-docs.version", String.class);
    }

    public static String[] getSpringdocPackagesToScan() {
        String list = get("springdoc.packagesToScan", String.class);
        return list.split(",");
    }

    public static String getAppJwtSecret() {
        return get("app.jwt-secret", String.class);
    }

    public static Integer getAppJwtExpirationMinutes() {
        return Integer.valueOf(get("app.jwt-expiration-minutes", String.class));
    }

    public static String getSpringDatasourceUrl() {
        return get("spring.datasource.url", String.class);
    }

    public static String getSpringDatasourceUsername() {
        return get("spring.datasource.username", String.class);
    }

    public static String getSpringDatasourcePassword() {
        return get("spring.datasource.password", String.class);
    }

    public static String getSpringDatasourceDriverClassName() {
        return get("spring.datasource.driver-class-name", String.class);
    }

    public static String getSpringJpaPropertiesHibernateDialect() {
        return get("spring.jpa.properties.hibernate.dialect", String.class);
    }

    public static Boolean getSpringJpaShowSql() {
        return Boolean.valueOf(get("spring.jpa.show-sql", String.class));
    }

    public static String getSpringJpaHibernateDdlAuto() {
        return get("spring.jpa.hibernate.ddl-auto", String.class);
    }

    public static Integer getMariadbMaximumPoolSize() {
        return Integer.valueOf(get("mariadb.maximum-pool-size", String.class));
    }

    public static Integer getMariadbMinimumIdle() {
        return Integer.valueOf(get("mariadb.minimum-idle", String.class));
    }

    public static Integer getMariadbIdleTimeout() {
        return Integer.valueOf(get("mariadb.idleTimeout", String.class));
    }

    public static Integer getMariadbConnectionTimeout() {
        return Integer.valueOf(get("mariadb.connection-timeout", String.class));
    }

    public static String getMariadbPoolName() {
        return get("mariadb.pool-name", String.class);
    }

    public static String[] getCorsAllowedOrigins() {
        String result = get("cors.allowed-origins", String.class);
        if (result == null) {
            return new String[]{};
        }
        return result.trim().split(",");
    }

    public static Boolean getThymeleafCacheable() {
        return Boolean.valueOf(get("thymeleaf.cacheable", String.class));
    }

    public static String getEmailUsername() {
        return get("email.username", String.class);
    }

    public static String getEmailPassword() {
        return get("email.password", String.class);
    }

    public static String getEmailBaseUrl() {
        return get("email.base-url", String.class);
    }

    public static int getDatabaseHealthCheckerSchedulePeriodInSecond() {
        return Integer.valueOf(get("database-health-checker.schedule.period-in-second", String.class));
    }

    public static int getDatabaseHealthCheckerBackoffPeriodInSecond() {
        return Integer.valueOf(get("database-health-checker.backoff.period-in-second", String.class));
    }

    public static String getLogPath() {
        return get("log.path", String.class);
    }

    public static String getLogTimeZone() {
        return get("log.time-zone", String.class);
    }

    public static String getLogMaxFileSize() {
        return get("log.max-file-size", String.class);
    }

    public static String getLogArchiveFileNamePattern() {
        return get("log.archive.file-name-pattern", String.class);
    }

    public static Integer getLogArchiveMaxNumberFile() {
        return Integer.valueOf(get("log.archive.max-number-file", String.class));
    }

    public static String getLogArchiveTotalSizeCap() {
        return get("log.archive.total-size-cap", String.class);
    }

    public static Boolean getLogConsoleActive() {
        return Boolean.valueOf(get("log.console.active", String.class));
    }

    public static String getVersion() {
        return get("version", String.class);
    }

    public static String getJacksonTimeZone() {
        return get("jackson.time-zone", String.class);
    }

    public static Integer getCookieAgeMinutes() {
        return Integer.valueOf(get("cookie.age.minutes", String.class));
    }

    public static int getLimitRatingCapacity() {
        return Integer.valueOf(get("limit-rating.capacity", String.class));
    }

    public static int getLimitRatingRefillTokens() {
        return Integer.valueOf(get("limit-rating.refill-tokens", String.class));
    }

    public static int getLimitRatingRefillDurationInSecond() {
        return Integer.valueOf(get("limit-rating.refill-duration-in-second", String.class));
    }


    public static List<Map<String, Object>> getLimitRatingList() {
        String value = get("limit-rating.list", String.class);
        try {
            return new ObjectMapper().readValue(value, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }


}
