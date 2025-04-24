package com.arpo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arpo.models.CategoryProduct;



public interface ICategoryRepository extends JpaRepository<CategoryProduct, Long>{

}
