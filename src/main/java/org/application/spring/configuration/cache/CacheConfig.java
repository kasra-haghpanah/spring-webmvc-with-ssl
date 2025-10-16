package org.application.spring.configuration.cache;

import org.application.spring.configuration.properties.Properties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.AbstractCacheResolver;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Configuration
@EnableCaching
public class CacheConfig {

    private static Date lastDate = new Date();
    private Integer delay = Properties.getCacheFlushFixedDelaySecond();

    private static Date addDateBySeconds(int second) {
        LocalDateTime dateTime = LocalDateTime.now().plus(Duration.of(second, ChronoUnit.SECONDS));
        Date tmfn = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        return tmfn;
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("products", "users");
    }


    @Bean(name = "productCacheResolver")
    public CacheResolver cacheResolver() {

        Integer delay = Properties.getCacheFlushFixedDelaySecond();

        CacheResolver cacheResolver = new AbstractCacheResolver(cacheManager()) {
            @Override
            protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {

                if (lastDate.compareTo(new Date()) < 0) {
                    cacheManager().getCacheNames().stream().forEach(cacheName -> {
                        if (cacheName.equals("products")) {
                            cacheManager().getCache(cacheName).clear();
                        }
                    });
                    lastDate = addDateBySeconds(delay);
                }

                return Arrays.asList("products");
            }
        };
        return cacheResolver;
    }
}
