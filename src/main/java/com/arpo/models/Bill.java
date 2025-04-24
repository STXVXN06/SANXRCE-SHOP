package com.arpo.models;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity(name = "bill")
public class Bill implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idBill;
	
	private double totalPrice;
	
	private int idDetailBill;
	
	private Date date;
	
	@OneToOne
	@JoinColumn(name="FacturaPedido")
	private Order order;

	public Bill(Long idBill, double totalPrice, int idDetailBill, Date date, Order order) {
		super();
		this.idBill = idBill;
		this.totalPrice = totalPrice;
		this.idDetailBill = idDetailBill;
		this.date = date;
		this.order = order;
	}

	public Long getIdBill() {
		return idBill;
	}

	public void setIdBill(Long idBill) {
		this.idBill = idBill;
	}

	public double gettotalPrice() {
		return totalPrice;
	}

	public void settotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public int getIdDetailBill() {
		return idDetailBill;
	}

	public void setIdDetailBill(int idDetailBill) {
		this.idDetailBill = idDetailBill;
	}

	public Date getdate() {
		return date;
	}

	public void setdate(Date date) {
		this.date = date;
	}

	public Order getPedido() {
		return order;
	}

	public void setPedido(Order order) {
		this.order = order;
	}
	
}
