package com.arpo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arpo.models.Cart;



public interface ICartRepository extends JpaRepository<Cart, Long>{
	
}
