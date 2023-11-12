package com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
public class ReviewEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ratingId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private ProductEntity product;

	
	@Min(value = 1, message = "La calificación debe ser al menos 1")
    @Max(value = 5, message = "La calificación debe ser como máximo 5")
	private int rating;

	private String comment;
	
	

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Long getRatingId() {
		return ratingId;
	}

	public void setRatingId(Long ratingId) {
		this.ratingId = ratingId;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public ProductEntity getProduct() {
		return product;
	}

	public void setProduct(ProductEntity product) {
		this.product = product;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

}
