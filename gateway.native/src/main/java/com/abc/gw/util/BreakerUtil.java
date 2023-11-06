package com.abc.gw.util;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;

import com.abc.gw.breaker.CustomReactiveCircuitBreaker;
import com.abc.gw.model.BreakerApi;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;

public final class BreakerUtil {

	private static final Map<String, ReactiveCircuitBreaker> breakers = new ConcurrentHashMap<String, ReactiveCircuitBreaker>();
	
	public static final ReactiveCircuitBreaker getBreaker(String sid, BreakerApi BreakerApi) {
		String id = BreakerApi.getApiPattern();
		String key = sid + id;
		if (breakers.containsKey(key)) {
			return breakers.get(key);
		}
		CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
				.slowCallDurationThreshold(Duration.ofMillis(BreakerApi.getTimeout())).build();
		CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
		TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.ofDefaults();

		TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
				.timeoutDuration(Duration.ofMillis(BreakerApi.getTimeout())).build();
		ReactiveCircuitBreaker reactiveCircuitBreaker = new CustomReactiveCircuitBreaker(id, sid, circuitBreakerConfig,
				circuitBreakerRegistry, timeLimiterRegistry, timeLimiterConfig);
		breakers.put(key, reactiveCircuitBreaker);
		return reactiveCircuitBreaker;
	}
	
	public static final void refreshBreaker() {
		breakers.clear();
	}
}
