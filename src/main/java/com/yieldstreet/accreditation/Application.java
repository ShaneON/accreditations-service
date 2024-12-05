package com.yieldstreet.accreditation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

	@Bean
	public ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		return mapper;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
