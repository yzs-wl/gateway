package com.abc.gw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.web.reactive.config.EnableWebFlux;

import com.abc.gw.config.GatewayConfig;

@SpringBootApplication
@EnableWebFlux
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Import(value = { GatewayConfig.class })
public class GatewayServer {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServer.class, args);
	}
}