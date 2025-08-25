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

    public static record Policy(String path, int capacity, int refillTokens, Duration refillDuration) {
    }

}
