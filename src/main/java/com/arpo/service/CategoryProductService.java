package com.arpo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arpo.models.CategoryProduct;
import com.arpo.repository.ICategoryRepository;



@Service
public class CategoryProductService {
	
	@Autowired
	private ICategoryRepository categoryRepository;
	
	public List<CategoryProduct> listCategory(){
		return categoryRepository.findAll();
	}
	
	public boolean alReadyExist(Long id) {
		Optional<CategoryProduct> categoryExist = categoryRepository .findById(id);
		return categoryExist.isPresent();
	}
	
	public CategoryProduct getById (Long id) {
		return categoryRepository.findById(id).orElse(null);
	}
	
	public CategoryProduct save (CategoryProduct categoryProduct) {
		return categoryRepository.save(categoryProduct);
	}

	public void delete(Long id) {
		categoryRepository.deleteById(id);
	}
}
