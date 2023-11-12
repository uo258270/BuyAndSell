package com.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="CART")
public class ShoppingCartEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dateCreated;

	@Id
	@Column(name = "ID", length = 50, nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	//buyer
	@ManyToOne
	private UserEntity user;
	
	@ManyToMany(fetch = FetchType.LAZY)
	private List<ProductEntity> products = new ArrayList<>();
	
	


	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<ProductEntity> getProducts() {
		return products;
	}

	public void setProducts(List<ProductEntity> products) {
		this.products = products;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public LocalDate getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDate dateCreated) {
		this.dateCreated = dateCreated;
	}

}
