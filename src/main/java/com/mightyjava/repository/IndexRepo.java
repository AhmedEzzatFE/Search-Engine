package com.mightyjava.repository;

import com.mightyjava.domain.SearchQuery;
import com.mightyjava.domain.Trends;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface IndexRepo extends PagingAndSortingRepository<Trends, Long> {

    @Query("From Trends b ")
    Page<Trends> findAllTrends(Pageable pageable);
}
