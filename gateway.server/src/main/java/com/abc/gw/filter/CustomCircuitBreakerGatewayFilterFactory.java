package com.abc.gw.filter;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;

import com.abc.gw.model.BreakerApi;
import com.abc.gw.model.BreakerConfig;
import com.abc.gw.util.BreakerUtil;

import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;

public class CustomCircuitBreakerGatewayFilterFactory
		extends AbstractGatewayFilterFactory<BreakerConfig> {

	private static final Logger logger = LoggerFactory.getLogger(CustomCircuitBreakerGatewayFilterFactory.class);

	private static final AntPathMatcher antPathMatcher= new AntPathMatcher();
	private static final String BEAN_NAME = "customCircuitBreaker";
	private static final String GATEWAY_BREAKE_MESSAGE = "the requests is slow, gateway timeout!";

	public CustomCircuitBreakerGatewayFilterFactory() {
		super(BreakerConfig.class);
	}

	@Override
	public GatewayFilter apply(BreakerConfig config) {
		return (exchange, chain) -> {
			String path = exchange.getRequest().getPath().pathWithinApplication().value();
			BreakerApi timeout = getBreakerApi(config, path);
			Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
			String sid = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_PREDICATE_MATCHED_PATH_ROUTE_ID_ATTR);
			if (route != null && timeout != null) {
				ReactiveCircuitBreaker reactiveCircuitBreaker = BreakerUtil.getBreaker(sid, timeout);
				return reactiveCircuitBreaker.run(chain.filter(exchange), throwable -> {
					exchange.getResponse().setStatusCode(HttpStatus.GATEWAY_TIMEOUT);
					if (logger.isInfoEnabled()) {
						logger.info("micro service [{}] api [{}] - {}", sid, path, GATEWAY_BREAKE_MESSAGE);
					}
					return exchange.getResponse().writeAndFlushWith(Flux.just(ByteBufFlux
							.just(exchange.getResponse().bufferFactory().wrap(GATEWAY_BREAKE_MESSAGE.getBytes()))));
				});
			} else {
				return chain.filter(exchange);
			}
		};
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Collections.singletonList(NAME_KEY);
	}

	private BreakerApi getBreakerApi(BreakerConfig config, String path) {
		List<BreakerApi> BreakerApiList = config.getBreakerApis();
		BreakerApi BreakerApi = null;
		for (BreakerApi BreakerApiPattern : BreakerApiList) {
			if (antPathMatcher.match(BreakerApiPattern.getApiPattern(), path)) {
				BreakerApi = BreakerApiPattern;
				break;
			}
		}
		return BreakerApi;
	}

	@Override
	public String name() {
		return BEAN_NAME;
	}
}
