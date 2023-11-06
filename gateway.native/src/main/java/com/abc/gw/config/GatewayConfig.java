
package com.abc.gw.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import com.abc.gw.filter.CustomCircuitBreakerGatewayFilterFactory;
import com.abc.gw.filter.CustomRateLimiterGatewayFilterFactory;
import com.abc.gw.filter.CustomReactiveLoadBalancerClientFilter;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@LoadBalancerClients(defaultConfiguration = { GatewayConfig.class })
public class GatewayConfig implements WebFluxConfigurer {

	@Bean
	public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
		http.csrf().disable().cors().disable().httpBasic().disable().formLogin().loginPage("/login").and()
				.authorizeExchange().pathMatchers(HttpMethod.OPTIONS).permitAll().pathMatchers("/**").permitAll()
				.anyExchange().authenticated();
		return http.build();
	}

	@Value("${spring.cloud.gateway.route.config.group:DEFAULT_GROUP}")
    private String group;

    @Value("${spring.cloud.gateway.route.config.dataid:gateway}")
    private String dataid;

    @Value("${spring.cloud.gateway.route.config.timeout:5000}")
    private int timeout;

    @Value("${spring.cloud.nacos.config.server-addr:127.0.0.1:8848}")
    private String serverAddr;
    
    @Value("${spring.cloud.nacos.config.username:nacos}")
    private String username;
    
    @Value("${spring.cloud.nacos.config.password:nacos}")
    private String password;
    
    
	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.setAllowCredentials(true);
		config.addAllowedMethod("*");
		config.addExposedHeader("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsWebFilter(source);
	}

	@Bean
	public CustomCircuitBreakerGatewayFilterFactory customCircuitBreakerGatewayFilterFactory() {
		return new CustomCircuitBreakerGatewayFilterFactory();
	}

	@Bean
	public CustomRateLimiterGatewayFilterFactory customRateLimiterGatewayFilterFactory() {
		return new CustomRateLimiterGatewayFilterFactory();
	}
	
	@Bean(name = "reactiveLoadBalancerClientFilter")
	public CustomReactiveLoadBalancerClientFilter customReactiveLoadBalancerClientFilter(LoadBalancerClientFactory clientFactory,
			GatewayLoadBalancerProperties properties) {
		return new CustomReactiveLoadBalancerClientFilter(clientFactory, properties);
	}
	
	@Bean
    public DynamicRoute dynamicRoute() {
		Properties properties = new Properties();
		properties.put("serverAddr", this.serverAddr);
		properties.put("username", this.username);
		properties.put("password", this.password);
        return new DynamicRoute(group, dataid, timeout, properties);
    }
}
