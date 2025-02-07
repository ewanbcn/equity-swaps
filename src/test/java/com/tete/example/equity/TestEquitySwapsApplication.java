package com.tete.example.equity;

import org.springframework.boot.SpringApplication;

import com.tete.example.equity.EquitySwapsApplication;

public class TestEquitySwapsApplication {

	public static void main(String[] args) {
		SpringApplication.from(EquitySwapsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
