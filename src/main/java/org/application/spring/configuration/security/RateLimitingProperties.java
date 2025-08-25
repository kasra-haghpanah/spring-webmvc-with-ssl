package org.application.spring.configuration.security;

import io.github.bucket4j.Bucket;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitingProperties {

    public static final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public final boolean enabled;
    public final Policy defaultPolicy;
    public final List<Policy> policies = new ArrayList<>();

    public RateLimitingProperties(boolean enabled, Policy defaultPolicy) {
        this.enabled = enabled;
        this.defaultPolicy = defaultPolicy;
    }

    public void add(Policy... policies) {
        for (Policy policy : policies) {
            this.policies.add(policy);
        }
    }

    public static class Policy {
        public final String path;
        public final int capacity;
        public final int refillTokens;
        public final Duration refillDuration;

        public Policy(String path, int capacity, int refillTokens, Duration refillDuration) {
            this.path = path;
            this.capacity = capacity;
            this.refillTokens = refillTokens;
            this.refillDuration = refillDuration;
        }


    }

}
