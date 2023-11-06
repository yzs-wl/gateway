package com.abc.gw.breaker;


import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.concurrent.TimeUnit;

import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class CustomReactiveCircuitBreaker implements ReactiveCircuitBreaker {

	private final String id;
	private final String groupName;
	private final CircuitBreakerRegistry circuitBreakerRegistry;
	private final TimeLimiterRegistry timeLimiterRegistry;
	private final CircuitBreakerConfig circuitBreakerConfig;
	private final TimeLimiterConfig timeLimiterConfig;
	
	public CustomReactiveCircuitBreaker(String id, String groupName, CircuitBreakerConfig circuitBreakerConfig,
			CircuitBreakerRegistry circuitBreakerRegistry, TimeLimiterRegistry timeLimiterRegistry, TimeLimiterConfig timeLimiterConfig) {
		this.id = id;
		this.groupName = groupName;
		this.circuitBreakerRegistry = circuitBreakerRegistry;
		this.timeLimiterRegistry = timeLimiterRegistry;
		this.circuitBreakerConfig = circuitBreakerConfig;
		this.timeLimiterConfig = timeLimiterConfig;
	}
	
	@Override
	public <T> Mono<T> run(Mono<T> toRun, Function<Throwable, Mono<T>> fallback) {
		Tuple2<CircuitBreaker, TimeLimiter> tuple = buildCircuitBreakerAndTimeLimiter();
		Mono<T> toReturn = toRun.transform(CircuitBreakerOperator.of(tuple.getT1()))
				.timeout(tuple.getT2().getTimeLimiterConfig().getTimeoutDuration())
				.doOnError(TimeoutException.class,
						t -> tuple.getT1().onError(tuple.getT2().getTimeLimiterConfig().getTimeoutDuration().toMillis(),
								TimeUnit.MILLISECONDS, t));
		if (fallback != null) {
			toReturn = toReturn.onErrorResume(fallback);
		}
		return toReturn;
	}

	@Override
	public <T> Flux<T> run(Flux<T> toRun, Function<Throwable, Flux<T>> fallback) {
		Tuple2<CircuitBreaker, TimeLimiter> tuple = buildCircuitBreakerAndTimeLimiter();
		Flux<T> toReturn = toRun.transform(CircuitBreakerOperator.of(tuple.getT1()))
				.timeout(tuple.getT2().getTimeLimiterConfig().getTimeoutDuration())
				.doOnError(TimeoutException.class,
						t -> tuple.getT1().onError(tuple.getT2().getTimeLimiterConfig().getTimeoutDuration().toMillis(),
								TimeUnit.MILLISECONDS, t));
		if (fallback != null) {
			toReturn = toReturn.onErrorResume(fallback);
		}
		return toReturn;
	}

	private Tuple2<CircuitBreaker, TimeLimiter> buildCircuitBreakerAndTimeLimiter() {
		final Map<String, String> tags = Map.of("group", this.groupName);
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(id, circuitBreakerConfig, tags);
		TimeLimiter timeLimiter = this.timeLimiterRegistry.find(this.id)
				.orElseGet(() -> this.timeLimiterRegistry.find(this.groupName)
						.orElseGet(() -> this.timeLimiterRegistry.timeLimiter(this.id, this.timeLimiterConfig, tags)));
		return Tuples.of(circuitBreaker, timeLimiter);
	}
}
