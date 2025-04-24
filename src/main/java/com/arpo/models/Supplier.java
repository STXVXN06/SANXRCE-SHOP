package com.arpo.models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Supplier implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idSupplier;
	
	private String nameSupplier;
	
	@Column(name = "numberSupplier")
	private String phoneNumber;
	
	@Column(name = "addressSupplier")
	private String address;

	public Supplier(Long idSupplier, String nameSupplier, String phoneNumber, String address) {
		super();
		this.idSupplier = idSupplier;
		this.nameSupplier = nameSupplier;
		this.phoneNumber = phoneNumber;
		this.address = address;
	}
	
	public Supplier() {
		super();
	}

	public Long getIdSupplier() {
		return idSupplier;
	}

	public void setIdSupplier(Long idSupplier) {
		this.idSupplier = idSupplier;
	}

	public String getNameSupplier() {
		return nameSupplier;
	}

	public void setNameSupplier(String nameSupplier) {
		this.nameSupplier = nameSupplier;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	
		
}