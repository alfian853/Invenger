package com.bliblifuture.Invenger;

import com.bliblifuture.Invenger.model.Admin;
import com.bliblifuture.Invenger.model.User;
import com.bliblifuture.Invenger.repository.AdminRepository;
import com.bliblifuture.Invenger.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sun.security.krb5.KrbCryptoException;

import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
public class InvengerApplication {

	@Autowired
	AdminRepository adminRepository;

	private static final Logger log = LoggerFactory.getLogger(InvengerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(InvengerApplication.class, args);
	}

	@Bean
	public CommandLineRunner tester(UserRepository userRepository){
		return args -> {
			Admin admin = adminRepository.findByName("root");
			if(admin == null){
				admin = new Admin();
				admin.setName("root");
				admin.setPassword(new BCryptPasswordEncoder().encode("invenger-root-777"));
				adminRepository.save(admin);
			}
		};
	}

}
