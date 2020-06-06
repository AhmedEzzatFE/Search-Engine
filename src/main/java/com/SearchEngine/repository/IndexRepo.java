package com.SearchEngine.repository;

import com.SearchEngine.domain.Trends;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface IndexRepo extends PagingAndSortingRepository<Trends, Long> {

    @Query("From Trends b where b.location=:Country ")
    Page<Trends> findAllTrends(Pageable pageable,@Param("Country")String Country);
}
