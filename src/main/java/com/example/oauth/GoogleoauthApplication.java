package com.example.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)

public class GoogleoauthApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoogleoauthApplication.class, args);
	}

}
