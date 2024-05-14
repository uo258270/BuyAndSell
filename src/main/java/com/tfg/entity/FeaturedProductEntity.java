package com.tfg.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class FeaturedProductEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long featuredId;
	
	private Date date;
	
	@ManyToOne
	@JoinColumn(name="productId")
	private ProductEntity product;
	
	
	@ManyToOne
	@JoinColumn(name="userId")
	private UserEntity user;
	
	
	

	public Long getFeaturedId() {
		return featuredId;
	}

	public void setFeaturedId(Long featuredId) {
		this.featuredId = featuredId;
	}

	public ProductEntity getProduct() {
		return product;
	}

	public void setProduct(ProductEntity product) {
		this.product = product;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, featuredId, product, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeaturedProductEntity other = (FeaturedProductEntity) obj;
		return Objects.equals(date, other.date) && Objects.equals(featuredId, other.featuredId)
				&& Objects.equals(product, other.product) && Objects.equals(user, other.user);
	}
	
	
	
}
