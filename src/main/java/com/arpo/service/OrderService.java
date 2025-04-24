package com.arpo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arpo.models.CategoryProduct;
import com.arpo.models.Order;
import com.arpo.models.Product;
import com.arpo.models.User;
import com.arpo.repository.IOrderRepository;

@Service
public class OrderService {

	@Autowired
	private IOrderRepository ordenRepository;

	
	public Order save(Order orden) {
		return ordenRepository.save(orden);
	}

	public Object findByIdOrder(Integer id) {
		return ordenRepository.findById(id);
	}

	public List<Order> findAll() {
		return ordenRepository.findAll();
	}
	
	public List<Order> findByUsuario(User usuario) {
		return ordenRepository.findByUser(usuario);
	}

	public Optional<Order> findById(Integer id) {
		return ordenRepository.findById(id);
	}
	
	public List<Order> getByStatus(String name){
		 return ordenRepository.findByStatus(name);
		 
	 } 
	
}
