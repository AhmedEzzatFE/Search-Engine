package com.SearchEngine.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IService<T> {
	Page<T> findAll(Pageable pageable,String Country,int id, String searchText,int isImage,int Erase);


}
