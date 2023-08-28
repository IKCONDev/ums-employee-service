package com.ikn.ums.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class UMSEmployeeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UMSEmployeeServiceApplication.class, args);
		
		System.out.println("UMSEmployeeServiceApplication.main() ENTERED");
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
