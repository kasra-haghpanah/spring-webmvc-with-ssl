package org.application.spring.configuration.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@DependsOn({"properties"})
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private final boolean enabled;
    private final Policy defaultPolicy;
    private final List<Policy> policies;

    /*
rate-limiting:
  enabled: true
  default-policy:
    capacity: 10
    refill-tokens: 10
    refill-duration: 1s
  policies:

    - path: /api/public/**
      capacity: 20
      refill-tokens: 20
      refill-duration: 1s

    - path: /api/private/**
      capacity: 5
      refill-tokens: 5
      refill-duration: 1s
    * */

    public RateLimitingFilter() {
        enabled = true;
        defaultPolicy = new Policy("/spring/**", 10, 10, Duration.ofSeconds(1));
        policies = new ArrayList<>();
        policies.add(new Policy("/spring/**", 20, 20, Duration.ofSeconds(1)));
       // policies.add(new Policy("/spring/**", 5, 5, Duration.ofSeconds(1)));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        Policy policy = matchPolicy(path);
        Bucket bucket = buckets.computeIfAbsent(path, p -> createBucket(policy));

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded");
        }
    }

    private Policy matchPolicy(String path) {
        return policies.stream()
                .filter(p -> path.matches(p.getPath().replace("**", ".*")))
                .findFirst()
                .orElse(defaultPolicy);
    }

    private Bucket createBucket(Policy policy) {
        Refill refill = Refill.intervally(policy.getRefillTokens(), policy.getRefillDuration());
        Bandwidth limit = Bandwidth.classic(policy.getCapacity(), refill);
        return Bucket.builder().addLimit(limit).build();
    }


    public static class Policy {
        private String path;
        private int capacity;
        private int refillTokens;
        private Duration refillDuration;

        public Policy() {
        }

        public Policy(String path, int capacity, int refillTokens, Duration refillDuration) {
            this.path = path;
            this.capacity = capacity;
            this.refillTokens = refillTokens;
            this.refillDuration = refillDuration;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getRefillTokens() {
            return refillTokens;
        }

        public void setRefillTokens(int refillTokens) {
            this.refillTokens = refillTokens;
        }

        public Duration getRefillDuration() {
            return refillDuration;
        }

        public void setRefillDuration(Duration refillDuration) {
            this.refillDuration = refillDuration;
        }
    }
}

