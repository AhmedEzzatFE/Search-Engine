package com.mightyjava.service.impl;

import com.mightyjava.Query_and_Ranker.QueryProcessorForWI;
import com.mightyjava.domain.Trends;
import com.mightyjava.repository.IndexRepo;
import com.mightyjava.service.TrendService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mightyjava.domain.SearchQuery;
import com.mightyjava.repository.BookRepository;
import com.mightyjava.service.IService;

@Service
public class TrendsServiceImpl implements TrendService<Trends> {

    @Autowired
    private IndexRepo indexRepo;

    @Override
    public Page<Trends> findAllTrends(Pageable pageable) {
        //hna hnady el trend
        return indexRepo.findAllTrends(pageable);
    }



    @Override
    public Page<Trends> findAll(Pageable pageable) {
        return indexRepo.findAll(pageable);
    }



}
