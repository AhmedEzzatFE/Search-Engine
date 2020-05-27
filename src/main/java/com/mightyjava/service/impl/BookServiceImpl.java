package com.mightyjava.service.impl;

import com.mightyjava.Query_and_Ranker.ImageProcessorWI;
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
	public Page<SearchQuery> findAll(Pageable pageable,String Country,int id, String searchText,int isImage,boolean erase) {

		if(isImage==0){
		QueryProcessorForWI x = new QueryProcessorForWI(searchText,Country,id,erase);
		try {
			x.Processor();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Search Query Finished");}
		else if(isImage==1){
			ImageProcessorWI y = new ImageProcessorWI(searchText,id);
			y.Processor_Image();
			System.out.println("Search Image Finished");
		}
		return bookRepository.findAllBooks(pageable,id,searchText,isImage);
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
