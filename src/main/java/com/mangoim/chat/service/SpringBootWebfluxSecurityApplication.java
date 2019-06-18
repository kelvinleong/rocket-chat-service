package com.mangoim.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableReactiveMongoRepositories
@Slf4j
public class SpringBootWebfluxSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxSecurityApplication.class, args);
	}

}
