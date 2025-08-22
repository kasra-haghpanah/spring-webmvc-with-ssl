package org.application.spring.configuration.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import jakarta.annotation.PostConstruct;
import net.logstash.logback.encoder.LogstashEncoder;
import org.application.spring.configuration.properties.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfiguration {

    @PostConstruct
    public void setupLogstashAppender() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        // تعریف encoder با فرمت Logstash
        LogstashEncoder encoder = new LogstashEncoder();
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
    }
}

