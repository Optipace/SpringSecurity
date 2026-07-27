package com.example.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AuthServiceApplication {
	public static void main(String[] args) {
//		System.out.println("1. Before SpringApplication.run");

//		try {
			SpringApplication.run(AuthServiceApplication.class, args);
//			System.out.println("2. Application started");
//		} catch (Throwable t) {
//			t.printStackTrace();
		}

//		System.out.println("3. End of main");
	}
//		SpringApplication.run(AuthServiceApplication.class, args);


