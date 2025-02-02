package com.example.demo;

import org.springframework.boot.SpringApplication;


public class TestDemoApplication {

	public static void main(String[] args) {
		// none datasource for profile `dev`, it will use the default HikariPool pool
		System.getProperties().putIfAbsent("spring.profiles.active", "dev");
		SpringApplication.from(DemoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
