package com.SearchEngine.service.impl;

import com.SearchEngine.CR_IND_QP_RK.TrendsWI;
import com.SearchEngine.domain.Trends;
import com.SearchEngine.repository.IndexRepo;
import com.SearchEngine.service.TrendService;
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
    public Page<Trends> findAllTrends(Pageable pageable,int id,String Country) {
        TrendsWI x= new TrendsWI(id);
        try {
            x.GetTrends();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Trends Finished");
        return indexRepo.findAllTrends(pageable,Country);
    }



}
