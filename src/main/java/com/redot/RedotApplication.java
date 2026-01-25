package com.redot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RedotApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedotApplication.class, args);
	}

}
