
package com.abc.gw.config;

import java.net.URI;

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
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
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

	public ServerLogoutSuccessHandler logoutSuccessHandler(String uri) {
		RedirectServerLogoutSuccessHandler successHandler = new RedirectServerLogoutSuccessHandler();
		successHandler.setLogoutSuccessUrl(URI.create(uri));
		return successHandler;
	}

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
}
