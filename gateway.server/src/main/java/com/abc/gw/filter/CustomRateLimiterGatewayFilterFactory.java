package com.abc.gw.filter;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;

import com.abc.gw.model.LimiterApi;
import com.abc.gw.model.LimiterConfig;
import com.abc.gw.util.LimiterUtil;

import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;

public class CustomRateLimiterGatewayFilterFactory
		extends AbstractGatewayFilterFactory<LimiterConfig> {

	private static final Logger logger = LoggerFactory.getLogger(CustomCircuitBreakerGatewayFilterFactory.class);

	private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
	private static final String BEAN_NAME = "customRateLimiter";
	private static final String GATEWAY_LIMIT_MESSAGE = "too many requests, gateway limit!";

	public CustomRateLimiterGatewayFilterFactory() {
		super(LimiterConfig.class);
	}

	@Override
	public GatewayFilter apply(LimiterConfig config) {
		return (exchange, chain) -> {
			String path = exchange.getRequest().getPath().pathWithinApplication().value();
			LimiterApi limit = getLimiterApi(config, path);
			Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
			String sid = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_PREDICATE_MATCHED_PATH_ROUTE_ID_ATTR);
			if (route != null && limit != null) {
				if (LimiterUtil.shouldLimit(sid, limit)) {
					exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
					if (logger.isInfoEnabled()) {
						logger.info("micro service [{}] api [{}] - {}", sid, path, GATEWAY_LIMIT_MESSAGE);
					}
					return exchange.getResponse().writeAndFlushWith(Flux.just(ByteBufFlux
							.just(exchange.getResponse().bufferFactory().wrap(GATEWAY_LIMIT_MESSAGE.getBytes()))));
				} else {
					return chain.filter(exchange);
				}
			} else {
				return chain.filter(exchange);
			}
		};
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Collections.singletonList(NAME_KEY);
	}

	private LimiterApi getLimiterApi(LimiterConfig config, String path) {
		List<LimiterApi> LimiterApiList = config.getLimiterApis();
		LimiterApi LimiterApi = null;
		for (LimiterApi LimiterApiPattern : LimiterApiList) {
			if (antPathMatcher.match(LimiterApiPattern.getApiPattern(), path)) {
				LimiterApi = LimiterApiPattern;
				break;
			}
		}
		return LimiterApi;
	}

	@Override
	public String name() {
		return BEAN_NAME;
	}
}