package com.example.aucation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AucationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AucationApplication.class, args);
	}

}
