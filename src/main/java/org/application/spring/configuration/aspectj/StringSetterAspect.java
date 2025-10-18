package org.application.spring.configuration.aspectj;

import org.application.spring.configuration.security.SecurityConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class StringSetterAspect {

    @Pointcut("execution(void org.application.spring.ddd.dto..*.set*(String)) || execution(void org.application.spring.ddd.model.entity..*.set*(String))")
    public void stringSetterMethods() {}

    @Around("stringSetterMethods() && args(value)")
    public void aroundStringSetter(ProceedingJoinPoint pjp, String value) throws Throwable {
        if (value != null) {
            String modifiedValue = SecurityConfig.sanitize(value);
            pjp.proceed(new Object[]{modifiedValue});
        } else {
            pjp.proceed(new Object[]{null});
        }
    }
}

