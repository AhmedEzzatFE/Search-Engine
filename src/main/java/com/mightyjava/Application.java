package com.mightyjava;

import com.mightyjava.domain.SearchQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mightyjava.service.IService;

@SpringBootApplication
public class Application {
	
	@Autowired
	private IService<SearchQuery> service;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}



}
