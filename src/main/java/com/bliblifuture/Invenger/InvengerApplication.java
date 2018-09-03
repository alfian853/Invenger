package com.bliblifuture.Invenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InvengerApplication {
    public static final Logger log = LoggerFactory.getLogger(InvengerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(InvengerApplication.class, args);
	}

}
