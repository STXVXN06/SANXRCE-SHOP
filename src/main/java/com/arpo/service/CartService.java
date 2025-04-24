package com.arpo.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arpo.models.Cart;
import com.arpo.repository.ICartRepository;




@Service
public class CartService {
    
    @Autowired
    private ICartRepository cartRepository;
    
    public Cart save(Cart cart) {
    	return cartRepository.save(cart);
    }
    
    public Optional<Cart> findById (Long id) {
    	return cartRepository.findById(id);
    }
    
}

