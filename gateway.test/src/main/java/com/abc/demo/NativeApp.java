package com.abc.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class NativeApp {

	public static void main(final String[] args) {
		SpringApplication.run(NativeApp.class, args);
	}
	
	@GetMapping("/test")
    public String get() {
        return "hello, abc";
    }
    
	@GetMapping("/limit1")
    public String limit1() {
        return "limit1 is ok";
    }
	
	@GetMapping("/limit2")
    public String limit2() {
        return "limit2 is ok";
    }
	
	@GetMapping("/timeout1")
    public String timeout1(@RequestParam(value = "t", defaultValue = "5") long time) {
	    assert(time > 1 && time <= 60);
	    try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "timeout1 is ok";
    }
	
	@GetMapping("/timeout2")
    public String timeout2(@RequestParam(value = "t", defaultValue = "8") long time) {
	    assert(time > 1 && time <= 60);
	    try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "timeout2 is ok";
    }
}