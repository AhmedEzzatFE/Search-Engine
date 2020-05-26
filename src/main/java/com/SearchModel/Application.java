package com.SearchModel;

import com.SearchModel.domain.SearchQuery;
import com.SearchModel.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	
	@Autowired
	private IService<SearchQuery> service;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}



}
