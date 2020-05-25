package com.mightyjava.resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface Resource<T> {
	@GetMapping("/search/{Country}/{id}/{searchText}/{isImage}")
	ResponseEntity<Page<T>> findAll(Pageable pageable,@PathVariable String Country, @PathVariable int id,@PathVariable String searchText,@PathVariable int isImage);


	@GetMapping
	ResponseEntity<Page<T>> findAll(int pageNumber, int pageSize);
	

	@PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	ResponseEntity<T> save(@RequestBody T t);

}
