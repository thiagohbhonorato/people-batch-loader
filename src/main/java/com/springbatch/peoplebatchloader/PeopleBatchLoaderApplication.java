package com.springbatch.peoplebatchloader;

import com.springbatch.peoplebatchloader.properties.ApplicationProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ ApplicationProperties.class })
public class PeopleBatchLoaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(PeopleBatchLoaderApplication.class, args);
	}

}
