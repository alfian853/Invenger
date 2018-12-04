package com.bliblifuture.invenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InvengerApplication {
	public static void main(String[] args) {
		SpringApplication.run(InvengerApplication.class, args);
	}
}
