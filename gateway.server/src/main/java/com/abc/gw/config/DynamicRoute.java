package com.abc.gw.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.abc.gw.model.RouteConfig;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import reactor.core.publisher.Mono;

public class DynamicRoute implements ApplicationEventPublisherAware {

	private static final Logger logger = LoggerFactory.getLogger(DynamicRoute.class);
	
	@Autowired
	private RouteDefinitionWriter writer;

	private ApplicationEventPublisher publisher;

	private String group;

	private String dataid;

	private int timeout;

	private Properties properties;
	
	private static final List<String> routes = Collections.synchronizedList(new ArrayList<>());

	public DynamicRoute(String groupName, String dataId, int time, Properties properties) {
		this.group = groupName;
		this.dataid = dataId;
		this.timeout = time;
		this.properties = properties;
	}

	@PostConstruct
	public void init() {
		try {
			ConfigService configService = NacosFactory.createConfigService(this.properties);
			String initRoute = configService.getConfig(this.dataid, this.group, this.timeout);
			this.refreshRoute(initRoute);
			configService.addListener(this.dataid, this.group, new Listener() {
				@Override
				public void receiveConfigInfo(String configInfo) {
					refreshRoute(configInfo);
				}

				@Override
				public Executor getExecutor() {
					return null;
				}
			});
		} catch (NacosException e) {
			if (logger.isErrorEnabled()) {
				logger.error("init route config error!", e);
			}
		}
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
	}

	private void publish() {
		this.publisher.publishEvent(new RefreshRoutesEvent(this.writer));
	}

	private void refreshRoute(String configInfo) {
		try {
			if (configInfo != null && configInfo.length() > 0) {
				ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
				mapper.findAndRegisterModules();
				try {
					RouteConfig routeConfig = mapper.readValue(configInfo, RouteConfig.class);
					if (routeConfig != null && routeConfig.getRouteDefinition().size() > 0) {
						clearRoute();
						routeConfig.getRouteDefinition().parallelStream().forEach(this::addRoute);
					}
					publish();
					if (logger.isInfoEnabled()) {
						logger.info("dynamic load gateway route success {}", configInfo);
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("refresh route config error!", e);
					}
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("refresh route config error!", e);
			}
		}
	}

	private void clearRoute() {
		routes.stream().forEach(id -> {
			this.writer.delete(Mono.just(id)).subscribe();
		});
		routes.clear();
	}

	private void addRoute(RouteDefinition definition) {
		try {
			if (this.writer != null) {
				this.writer.save(Mono.just(definition)).subscribe();
				routes.add(definition.getId());
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("add route error!", e);
			}
		}
	}
}