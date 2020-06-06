package com.SearchEngine.resource.impl;

import com.SearchEngine.domain.Trends;
import com.SearchEngine.resource.TrendResource;
import com.SearchEngine.service.TrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Trending")
@CrossOrigin(origins="*")
public class TrendsResourceImpl implements TrendResource<Trends> {

    @Autowired
    private TrendService<Trends> trendService;


    @Override
    public ResponseEntity<Page<Trends>> findAll(Pageable pageable,int id,String Country) {
        return new ResponseEntity<>(trendService.findAllTrends(pageable,id,Country), HttpStatus.OK);
    }

}
