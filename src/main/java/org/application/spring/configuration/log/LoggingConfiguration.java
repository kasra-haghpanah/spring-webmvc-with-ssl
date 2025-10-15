package org.application.spring.configuration.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import jakarta.annotation.PostConstruct;
import net.logstash.logback.encoder.LogstashEncoder;
import org.application.spring.configuration.properties.Properties;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class LoggingConfiguration {

    @PostConstruct
    public void setupLogAppender() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.detachAndStopAllAppenders(); // حذف اپندرهای قبلی
        rootLogger.setLevel(Level.INFO);
        String path = Properties.getLogPath();
        if (path != null && !path.isEmpty()) {
            rootLogger.addAppender(setupLogstashLogging(context, path));
        }

        if (Properties.getLogConsoleActive()) {
            rootLogger.addAppender(setupConsoleLogging(context));
        }

    }

    public Appender<ILoggingEvent> setupLogstashLogging(LoggerContext context, String path) {
        // تعریف encoder با فرمت Logstash
        LogstashEncoder encoder = new LogstashEncoder();
        encoder.setVersion(Properties.getVersion());
        //encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n");
        encoder.setIncludeCallerData(true);
        encoder.setIncludeContext(true);
        //encoder.setCustomFields("{\"app\":\"spring-webmvc-with-ssl\",\"env\":\"dev\"}");
        encoder.setContext(context);
        encoder.start();
        // تعریف فایل لاگ
        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setContext(context);
        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.setName("ROLLING_JSON_FILE");

        String archivePath = path;
        int archivePathSlashIndex = archivePath.lastIndexOf("/");
        if (archivePathSlashIndex > -1) {
            archivePath = path.substring(0, archivePathSlashIndex) + "/archive/" + path.substring(archivePathSlashIndex + 1);
        } else {
            archivePath = "/archive/" + path;
        }

        // پالیسی رولینگ بر اساس سایز و زمان
        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(rollingFileAppender);
        rollingPolicy.setFileNamePattern(archivePath + ".%d{yyyy-MM-dd}.%i.log");
        rollingPolicy.setMaxFileSize(FileSize.valueOf("1MB")); // حداکثر سایز هر فایل
        rollingPolicy.setMaxHistory(30);      // نگهداری ۳۰ فایل آرشیوی
        rollingPolicy.setTotalSizeCap(FileSize.valueOf("1GB")); // حداکثر حجم کل آرشیو
        rollingPolicy.setFileNamePattern(archivePath + ".%d{yyyy-MM-dd}.%i.log");
        rollingPolicy.start();

        rollingFileAppender.setRollingPolicy(rollingPolicy);
        rollingFileAppender.setFile(path); // فایل اصلی
        rollingFileAppender.start();

        ThresholdFilter infoFilter = new ThresholdFilter();
        infoFilter.setLevel("INFO");
        infoFilter.start();
        rollingFileAppender.addFilter(infoFilter);

        return rollingFileAppender;

    }


    public Appender<ILoggingEvent> setupConsoleLogging(LoggerContext context) {

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} %-5level [%thread] %logger{36} - %msg%n");
        encoder.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(context);
        consoleAppender.setEncoder(encoder);

        ThresholdFilter errorFilter = new ThresholdFilter();
        errorFilter.setLevel(Level.ERROR.toString());
        errorFilter.start();
        consoleAppender.addFilter(errorFilter);
        consoleAppender.start();
        return consoleAppender;


    }

}

