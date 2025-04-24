package com.arpo.models;



import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity(name ="categoryProduct")
public class CategoryProduct implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idCategoryProduct;
	
	private String nameCategory;
	
	public CategoryProduct(Long idCategoryProduct, String nameCategory) {
		super();
		this.idCategoryProduct = idCategoryProduct;
		this.nameCategory = nameCategory;

	}
	
	public CategoryProduct() {
		super();
	}

	public Long getIdCategoryProduct() {
		return idCategoryProduct;
	}

	public void setIdCategoryProduct(Long idCategoryProduct) {
		this.idCategoryProduct = idCategoryProduct;
	}

	public String getNameCategory() {
		return nameCategory;
	}

	public void setNameCategory(String nameCategory) {
		this.nameCategory = nameCategory;
	}

	

	

}