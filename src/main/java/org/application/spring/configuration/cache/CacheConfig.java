package org.application.spring.configuration.cache;

import org.application.spring.configuration.properties.Properties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.AbstractCacheResolver;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Configuration
@EnableCaching
@DependsOn({"properties"})
public class CacheConfig {

    // مقدار اولیه lastDate برابر با زمان فعلی + delay تنظیم می‌شود
    private static Date lastDate = null;

    // متغیر delay از تنظیمات گرفته می‌شود
    private final Integer delay;

    // متدی برای افزودن ثانیه به زمان فعلی
    private static Date addDateBySeconds(int second) {
        LocalDateTime dateTime = LocalDateTime.now().plus(Duration.of(second, ChronoUnit.SECONDS));
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public CacheConfig() {
        this.delay = Properties.getCacheFlushFixedDelaySecond();
        CacheConfig.lastDate = addDateBySeconds(delay);

    }

    // تعریف CacheManager با کش‌های products و users
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("products", "users");
    }

    // تعریف CacheResolver برای کش products با منطق پاک‌سازی دوره‌ای
    @Bean(name = "cacheResolver")
    public CacheResolver cacheResolver(CacheManager cacheManager) {
        return new AbstractCacheResolver(cacheManager) {
            @Override
            protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {

                // اگر زمان فعلی از lastDate عبور کرده باشد، کش پاک می‌شود
                if (new Date().after(lastDate)) {
                    cacheManager.getCacheNames().forEach(cacheName -> {
                        if ("products".equals(cacheName)) {
                            cacheManager.getCache(cacheName).clear();
                        }
                    });

                    // تنظیم مجدد lastDate برای ۶۰ ثانیه بعد
                    lastDate = addDateBySeconds(delay);
                }

                // بازگرداندن نام کش مورد نظر
                return Arrays.asList("products");
            }
        };
    }
}

