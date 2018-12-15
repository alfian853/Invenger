package com.bliblifuture.invenger;

import com.bliblifuture.invenger.config.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class InvengerApplication {
	public static void main(String[] args) {
		SpringApplication.run(InvengerApplication.class, args);
	}
}
