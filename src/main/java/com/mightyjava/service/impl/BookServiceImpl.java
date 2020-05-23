package com.mightyjava.service.impl;

import com.mightyjava.Query_and_Ranker.QueryProcessorForWI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mightyjava.domain.SearchQuery;
import com.mightyjava.repository.BookRepository;
import com.mightyjava.service.IService;

@Service
public class BookServiceImpl implements IService<SearchQuery> {

	@Autowired
	private BookRepository bookRepository;

	@Override
	public Page<SearchQuery> findAll(Pageable pageable, String searchText) {
		QueryProcessorForWI x = new QueryProcessorForWI(searchText);
		boolean temp = x.Processor();
		return bookRepository.findAllBooks(pageable);
	}

	@Override
	public Page<SearchQuery> getcountry(Pageable pageable, String country) {
		System.out.println("Your Country is :"+country);
		return null;
	}

	@Override
	public Page<SearchQuery> findAll(Pageable pageable) {
		return bookRepository.findAll(pageable);
	}

//
	@Override
	public SearchQuery saveOrUpdate(SearchQuery searchQuery) {
		return bookRepository.save(searchQuery);
	}

}
