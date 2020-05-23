package com.mightyjava.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IService<T> {
	Page<T> findAll(Pageable pageable, String searchText);
	Page<T> getcountry(Pageable pageable, String country);
	Page<T> findAll(Pageable pageable);

	T saveOrUpdate(T t);
	

}
