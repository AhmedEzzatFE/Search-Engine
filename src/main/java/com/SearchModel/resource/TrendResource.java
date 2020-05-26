package com.SearchModel.resource;

import com.SearchModel.domain.Trends;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


public interface TrendResource<T> {
    @GetMapping("/trend/{id}/{Country}")
    ResponseEntity<Page<T>> findAll(Pageable pageable,@PathVariable("id")int id,@PathVariable("Country")String Country);


    @GetMapping
    ResponseEntity<Page<T>> findAll(int pageNumber, int pageSize);

}
