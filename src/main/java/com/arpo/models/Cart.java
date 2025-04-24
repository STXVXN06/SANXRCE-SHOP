package com.arpo.models;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Cart implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idcart;
	
	private String nombre;
	private int cantidad;
	private double precio;
	private double total;
	
	@ManyToOne
	private Order order;
	
	@ManyToOne
	private Product product;
	
	public Cart() {
		// TODO Auto-generated constructor stub
	}

	public Cart(Long idcart, String nombre, int cantidad, double precio, double total, Order order,
			Product product) {
		super();
		this.idcart = idcart;
		this.nombre = nombre;
		this.cantidad = cantidad;
		this.precio = precio;
		this.total = total;
		this.order = order;
		this.product = product;
	}



	public Long getIdcart() {
		return idcart;
	}


	public void setIdcart(Long idcart) {
		this.idcart = idcart;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public int getCantidad() {
		return cantidad;
	}


	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}


	public double getPrecio() {
		return precio;
	}


	public void setPrecio(double precio) {
		this.precio = precio;
	}


	public double getTotal() {
		return total;
	}


	public void setTotal(double total) {
		this.total = total;
	}


	public Order getOrder() {
		return order;
	}


	public void setOrder(Order order) {
		this.order = order;
	}


	public Product getProduct() {
		return product;
	}


	public void setProduct(Product product) {
		this.product = product;
	}

}