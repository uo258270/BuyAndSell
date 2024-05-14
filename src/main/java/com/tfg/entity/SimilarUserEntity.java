package com.tfg.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class SimilarUserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private double similarity;

	@ManyToOne
	@JoinColumn(name = "user1_id")
	private UserEntity user1;

	@ManyToOne
	@JoinColumn(name = "user2_id")
	private UserEntity user2;
	

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

	public UserEntity getUser1() {
		return user1;
	}

	public void setUser1(UserEntity user1) {
		this.user1 = user1;
	}

	public UserEntity getUser2() {
		return user2;
	}

	public void setUser2(UserEntity user2) {
		this.user2 = user2;
	}

}
