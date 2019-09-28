package com.mangoim.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication(exclude = {
        ReactiveSecurityAutoConfiguration.class,
        MongoReactiveDataAutoConfiguration.class,
        MongoReactiveAutoConfiguration.class
})
@EnableWebFlux
@Slf4j
public class SpringBootWebfluxSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxSecurityApplication.class, args);
	}

}
