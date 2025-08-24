package org.application.spring.configuration.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import jakarta.annotation.PostConstruct;
import net.logstash.logback.encoder.LogstashEncoder;
import org.application.spring.configuration.properties.Properties;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.nio.charset.StandardCharsets;

@Configuration
public class LoggingConfiguration {

    @PostConstruct
    public void setupLogAppender() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        if (Properties.getLogPath() != null && !Properties.getLogPath().equals("")) {
            setupLogstashLogging(context);
        }

        if (Properties.getLogConsoleActive()) {
            setupConsoleLogging(context);
        }

    }

    public void setupLogstashLogging(LoggerContext context) {

        // تعریف encoder با فرمت Logstash
        LogstashEncoder encoder = new LogstashEncoder();

        encoder.setVersion(Properties.getVersion());
        //encoder.setCharset(StandardCharsets.UTF_8);
        //encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n");
        encoder.setIncludeCallerData(true);
        encoder.setIncludeContext(true);
        encoder.setCustomFields("{\"app\":\"spring-webmvc-with-ssl\",\"env\":\"dev\"}");
        encoder.setContext(context);
        encoder.start();

        // تعریف فایل لاگ

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setFile(Properties.getLogPath());
        fileAppender.setEncoder(encoder);
        fileAppender.setContext(context);
        fileAppender.setName("JSON_FILE");
        fileAppender.start();

        // اتصال به روت لاگر
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.detachAndStopAllAppenders(); // حذف اپندرهای قبلی
        rootLogger.addAppender(fileAppender);
        rootLogger.setLevel(Level.INFO);
    }


    public void setupConsoleLogging(LoggerContext context) {

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} %-5level [%thread] %logger{36} - %msg%n");
        encoder.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(context);
        consoleAppender.setEncoder(encoder);
        consoleAppender.start();

        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.ERROR); // فقط خطاها را نمایش بده
        rootLogger.addAppender(consoleAppender);
    }

}

