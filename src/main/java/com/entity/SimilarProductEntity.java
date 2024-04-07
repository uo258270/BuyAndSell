package com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class SimilarProductEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private double similarity;

	@ManyToOne
    @JoinColumn(name = "prod1_id")
    private ProductEntity prod1;

    @ManyToOne
    @JoinColumn(name = "prod2_id")
    private ProductEntity prod2;
    
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public ProductEntity getProd1() {
		return prod1;
	}

	public void setProd1(ProductEntity prod1) {
		this.prod1 = prod1;
	}

	public ProductEntity getProd2() {
		return prod2;
	}

	public void setProd2(ProductEntity prod2) {
		this.prod2 = prod2;
	}

}
