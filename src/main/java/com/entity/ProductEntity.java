package com.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRODUCT")
public class ProductEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;

	private String name;

	private String detail;

	private Date productDate;

	private Date updateDate;

	private Double price;

	private int stock;

	private String category;

	private int numOfViews;
	
	private boolean sold;

	private List<String> images;
	

	// relacion con user
	@ManyToOne
	@JoinColumn(name = "userId")
	private UserEntity user;

	@ManyToMany(fetch = FetchType.LAZY)
	private List<ShoppingCartEntity> carts;

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private List<ReviewEntity> reviews;

	public List<ReviewEntity> getReviews() {
		return reviews;
	}
	
	

	public boolean isSold() {
		return sold;
	}



	public void setSold(boolean sold) {
		this.sold = sold;
	}



	public void setReviews(List<ReviewEntity> reviews) {
		this.reviews = reviews;
	}

	public List<ShoppingCartEntity> getCarts() {
		return carts;
	}

	public void setCarts(List<ShoppingCartEntity> carts) {
		this.carts = carts;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Date getProductDate() {
		return productDate;
	}

	public void setProductDate(Date productDate) {
		this.productDate = productDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}


	

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public int getNumOfViews() {
		return numOfViews;
	}

	public void setNumOfViews(int numOfViews) {
		this.numOfViews = numOfViews;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}



	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public void decStock() {
		if (stock == 0) throw new IllegalArgumentException();
		stock--;
		
	}



	@Override
	public int hashCode() {
		return Objects.hash(productId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductEntity other = (ProductEntity) obj;
		return Objects.equals(productId, other.productId);
	}

	
	
	

}