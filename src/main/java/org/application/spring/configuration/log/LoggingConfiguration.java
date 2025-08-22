package org.application.spring.configuration.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import jakarta.annotation.PostConstruct;
import net.logstash.logback.composite.loggingevent.*;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;
import net.logstash.logback.encoder.LogstashEncoder;
import org.application.spring.configuration.properties.Properties;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class LoggingConfiguration {

    @PostConstruct
    public void setupJsonLogging() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        // تعریف providerهای JSON
        LoggingEventJsonProviders providers = new LoggingEventJsonProviders();
        providers.addTimestamp(new LoggingEventFormattedTimestampJsonProvider());
        providers.addLogLevel(new LogLevelJsonProvider());
        providers.addLoggerName(new LoggerNameJsonProvider());
        providers.addThreadName(new ThreadNameJsonProvider());
        providers.addMessage(new MessageJsonProvider());

        // ساخت encoder JSON
        LoggingEventCompositeJsonEncoder jsonEncoder = new LoggingEventCompositeJsonEncoder();
        jsonEncoder.setContext(context);
        jsonEncoder.setProviders(providers);
        //jsonEncoder.setVersion(Properties.getVersion());
        jsonEncoder.setEncoding(StandardCharsets.UTF_8.name());
        jsonEncoder.start();

        // ساخت FileAppender
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setContext(context);
        fileAppender.setName("JSON_FILE");
        fileAppender.setFile(Properties.getLogPath());
        fileAppender.setEncoder(jsonEncoder);
        fileAppender.setAppend(true);
        fileAppender.start();

        // گرفتن Logger از Logback و اضافه کردن Appender
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(fileAppender); // ← اینجا متد موجوده چون Logger از Logbackه
    }

    //@PostConstruct
/*    public void setupLogstashAppender() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

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
        ch.qos.logback.classic.Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.detachAndStopAllAppenders(); // حذف اپندرهای قبلی
        rootLogger.addAppender(fileAppender);
        rootLogger.setLevel(Level.INFO);
    }*/
}

