package com.tn.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TrendNavigatorServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrendNavigatorServerApplication.class, args);
	}

}
