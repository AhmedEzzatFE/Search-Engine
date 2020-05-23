package com.mightyjava.resource;

import com.mightyjava.domain.Trends;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


public interface TrendResource<T> {
    @GetMapping("/trend")
    ResponseEntity<Page<T>> findAll(Pageable pageable);


    @GetMapping
    ResponseEntity<Page<T>> findAll(int pageNumber, int pageSize);

}
