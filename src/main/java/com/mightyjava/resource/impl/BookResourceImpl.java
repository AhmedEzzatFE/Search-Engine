package com.mightyjava.resource.impl;

import com.mightyjava.domain.SearchQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mightyjava.resource.Resource;
import com.mightyjava.service.IService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BookResourceImpl implements Resource<SearchQuery> {
	
	@Autowired
	private IService<SearchQuery> bookService;

	//for searching by query
	@Override
	public ResponseEntity<Page<SearchQuery>> findAll(Pageable pageable,String Country,int id, String searchText,int isImage,int Erase) {
		return new ResponseEntity<>(bookService.findAll(pageable,Country,id, searchText,isImage,Erase), HttpStatus.OK);
	}


	//for page number searching
	@Override
	public ResponseEntity<Page<SearchQuery>> findAll(int pageNumber, int pageSize) {
		return new ResponseEntity<>(bookService.findAll(
				PageRequest.of(
						pageNumber, pageSize)
		), HttpStatus.OK);
	}
	@Override
	public ResponseEntity<SearchQuery> save(SearchQuery searchQuery) {
		return new ResponseEntity<>(bookService.saveOrUpdate(searchQuery), HttpStatus.CREATED);
	}
}
