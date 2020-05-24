package com.mightyjava.service.impl;

import com.mightyjava.Query_and_Ranker.QueryProcessorForWI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mightyjava.domain.SearchQuery;
import com.mightyjava.repository.BookRepository;
import com.mightyjava.service.IService;

import java.io.IOException;

@Service
public class BookServiceImpl implements IService<SearchQuery> {

	@Autowired
	private BookRepository bookRepository;

	@Override
	public Page<SearchQuery> findAll(Pageable pageable,String Country,int id, String searchText) {
		System.out.println("Your search is :"+searchText);

		QueryProcessorForWI x = new QueryProcessorForWI(searchText,Country,id);

		try {
			x.Processor();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Your Country is :"+Country);
		System.out.println("Your id is :"+id);


		return bookRepository.findAllBooks(pageable,id);
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
