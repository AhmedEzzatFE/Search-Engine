package com.mightyjava.service.impl;

import com.mightyjava.Query_and_Ranker.TrendsWI;
import com.mightyjava.domain.Trends;
import com.mightyjava.repository.IndexRepo;
import com.mightyjava.service.TrendService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class TrendsServiceImpl implements TrendService<Trends> {

    @Autowired
    private IndexRepo indexRepo;

    @Override
    public Page<Trends> findAllTrends(Pageable pageable,int id) {
        //hna hnady el trend
        System.out.println("hna hnady el trend");
        TrendsWI x= new TrendsWI(id);
        try {
            x.GetTrends();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return indexRepo.findAllTrends(pageable);
    }



    @Override
    public Page<Trends> findAll(Pageable pageable) {
        return indexRepo.findAll(pageable);
    }



}
