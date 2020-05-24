package com.mightyjava.resource.impl;

import com.mightyjava.domain.SearchQuery;
import com.mightyjava.domain.Trends;
import com.mightyjava.resource.TrendResource;
import com.mightyjava.service.TrendService;
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
@RequestMapping("/Trending")
@CrossOrigin(origins="http://localhost:3001")
public class TrendsResourceImpl implements TrendResource<Trends> {

    @Autowired
    private TrendService<Trends> trendService;


    @Override
    public ResponseEntity<Page<Trends>> findAll(Pageable pageable,int id) {
        return new ResponseEntity<>(trendService.findAllTrends(pageable,id), HttpStatus.OK);
    }
    @Override
    public ResponseEntity<Page<Trends>> findAll(int pageNumber, int pageSize) {
        return new ResponseEntity<>(trendService.findAll(
                PageRequest.of(
                        pageNumber, pageSize)
        ), HttpStatus.OK);
    }

}
