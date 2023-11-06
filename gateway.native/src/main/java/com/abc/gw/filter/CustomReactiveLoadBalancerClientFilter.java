package com.abc.gw.filter;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class CustomReactiveLoadBalancerClientFilter extends ReactiveLoadBalancerClientFilter {

	private static final Log log = LogFactory.getLog(CustomReactiveLoadBalancerClientFilter.class);

	@Autowired
	private ApplicationContext applicationContext;

	public CustomReactiveLoadBalancerClientFilter(LoadBalancerClientFactory clientFactory,
			GatewayLoadBalancerProperties properties) {
		super(clientFactory, properties);
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
		String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
		if (url == null || (!"lb".equals(url.getScheme()) && !"lb".equals(schemePrefix))) {
			return chain.filter(exchange);
		}
		addOriginalRequestUrl(exchange, url);
		if (log.isTraceEnabled()) {
			log.trace(ReactiveLoadBalancerClientFilter.class.getSimpleName() + " url before: " + url);
		}
		return choose(exchange).doOnNext(response -> {
			if (!response.hasServer()) {
				throw NotFoundException.create(true, "Unable to find instance for " + url.getHost());
			}
			ServiceInstance retrievedInstance = response.getServer();
			URI uri = exchange.getRequest().getURI();
			String overrideScheme = retrievedInstance.isSecure() ? "https" : "http";
			if (schemePrefix != null) {
				overrideScheme = url.getScheme();
			}
			DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(retrievedInstance,
					overrideScheme);
			URI requestUrl = reconstructURI(serviceInstance, uri);
			exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
		}).then(chain.filter(exchange));
	}

	protected URI reconstructURI(ServiceInstance serviceInstance, URI original) {
		return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
	}

	private Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange) {
		String sid = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_PREDICATE_MATCHED_PATH_ROUTE_ID_ATTR);
		List<ServiceInstance> servers = applicationContext.getBean(DiscoveryClient.class).getInstances(sid);
		if (servers.isEmpty()) {
			log.warn("No servers available for service: " + sid);
			return Mono.just(new EmptyResponse());
		}
		int index = ThreadLocalRandom.current().nextInt(servers.size());
		ServiceInstance instance = servers.get(index);
		return Mono.just(new DefaultResponse(instance));
	}
}