package com.abc.gw.util;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.gw.model.LimiterApi;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

public final class LimiterUtil {

	private static final Logger logger = LoggerFactory.getLogger(LimiterUtil.class);

	private static final int defaultTimeoutSeconds = 2;
	private static final int defaultTimeoutMillis = 100;
	private static final Map<String, RateLimiter> limiters = new ConcurrentHashMap<String, RateLimiter>();
	
	public static final boolean shouldLimit(String sid, LimiterApi LimiterApi) {
		String key = sid + LimiterApi.getApiPattern();
		RateLimiter rateLimiter = limiters.get(key);
		if (rateLimiter == null) {
			String limit = LimiterApi.getLimit();
			int time = 1;
			RateLimiterConfig config = null;
			try {
				if (limit.toLowerCase().endsWith("m")) {
					time = Integer.parseInt(limit.substring(0, limit.length() - 1));
					config = RateLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(defaultTimeoutSeconds))
							.limitForPeriod(time).limitRefreshPeriod(Duration.ofMinutes(1)).build();
				} else if (limit.toLowerCase().endsWith("s")) {
					time = Integer.parseInt(limit.substring(0, limit.length() - 1));
					config = RateLimiterConfig.custom().timeoutDuration(Duration.ofMillis(defaultTimeoutMillis))
							.limitForPeriod(time).limitRefreshPeriod(Duration.ofSeconds(1)).build();
				} else {
					time = Integer.parseInt(limit);
					config = RateLimiterConfig.custom().timeoutDuration(Duration.ofMillis(defaultTimeoutMillis))
							.limitForPeriod(time).limitRefreshPeriod(Duration.ofSeconds(1)).build();
				}
				RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(config);
				rateLimiter = rateLimiterRegistry.rateLimiter(key, config);
				limiters.put(key, rateLimiter);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("api limit [{}] config error!", limit);
				}
				return false;
			}
		}
		return !rateLimiter.acquirePermission();
	}
	
	public static final void refreshLimiter() {
		limiters.clear();
	}
}
