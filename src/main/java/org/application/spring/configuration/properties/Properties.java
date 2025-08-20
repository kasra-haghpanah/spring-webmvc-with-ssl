package org.application.spring.configuration.properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

@Configuration("properties")
public class Properties {

    public static String Localhost = "localhost";
    private static final Map<String, Object> config = new HashMap<String, Object>();

    public Properties(Environment environment) {

        config.put("server.port", environment.getProperty("server.port"));
        config.put("spring.application.name", environment.getProperty("spring.application.name"));
        config.put("springdoc.api-docs.version", environment.getProperty("springdoc.api-docs.version"));
        config.put("springdoc.packagesToScan", environment.getProperty("springdoc.packagesToScan"));
        config.put("app.jwt-secret", environment.getProperty("app.jwt-secret"));
        config.put("app.jwt-expiration-hours", environment.getProperty("app.jwt-expiration-hours"));
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

    }

    private static <T> T get(String key, Class<T> T) {
        return (T) config.get(key);
    }

    public static Integer getServerPort() {
        return Integer.valueOf(get("server.port", String.class));
    }

    public static String getApplicationName() {
        return get("spring.application.name", String.class);
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

    public static Integer getAppJwtExpirationHours() {
        return Integer.valueOf(get("app.jwt-expiration-hours", String.class));
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


}
