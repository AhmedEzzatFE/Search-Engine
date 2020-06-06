package com.SearchEngine.repository;

import com.SearchEngine.domain.SearchQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryRepository extends PagingAndSortingRepository<SearchQuery, Long> {

    @Query("From SearchQuery b where b.id=:id and b.searchquery=:searchText and b.image=:isImage order by b.Rank desc ")
    Page<SearchQuery> findAllUrls(Pageable pageable, @Param("id") int id, @Param("searchText") String searchText, @Param("isImage") int isImage);
}
