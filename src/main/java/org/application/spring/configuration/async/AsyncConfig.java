package org.application.spring.configuration.async;

import org.springframework.boot.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration(proxyBeanMethods = false)
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    // to inject this bean in your class
    @Bean
    public ExecutorService virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    // فعال‌سازی virtual threads برای Tomcat

    //  این Bean به صورت خودکار توسط Spring Boot در زمان راه‌اندازی Tomcat استفاده می‌شه، حتی اگر مستقیم inject نشده باشه.
    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadsCustomizer() {
        return protocolHandler ->
                protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    // فعال‌سازی virtual threads برای @Async
    @Override
    public Executor getAsyncExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }


}
