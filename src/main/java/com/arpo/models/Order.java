package com.arpo.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity (name = "orderUser")
public class Order implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idOrder;
	
	
    @Column(name = "statusOrder")
    private String status;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateOrder;
	
	@ManyToOne
	@JoinColumn (name = "UserOrder" )
	private User user;

	@OneToMany(mappedBy = "order")
	private List<Cart> detalle;
	
	private double total;
	
	
	public Order() {
		super();// TODO Auto-generated constructor stub
	}

	public int getIdOrder() {
		return idOrder;
	}

	public void setIdOrder(int idOrder) {
		this.idOrder = idOrder;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDateOrder() {
		return dateOrder;
	}

	public void setDateOrder(Date dateOrder) {
		this.dateOrder = dateOrder;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public List<Cart> getDetalle() {
		return detalle;
	}

	public void setDetalle(List<Cart> detalle) {
		this.detalle = detalle;
	}

	
	
	
}
