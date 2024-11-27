package com.miro.Laivanupotus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LaivanupotusApplication {

	public static void main(String[] args) {
		System.out.println("Initializing Laivanupotus.");
		SpringApplication.run(LaivanupotusApplication.class, args);
		System.out.println("Laivanupotus initialized.");

	}


}
