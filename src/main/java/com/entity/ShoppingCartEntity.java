package com.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.exception.ProductAlreadySoldException;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "CART")
public class ShoppingCartEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dateCreated;

	@Id
	@Column(name = "ID", length = 50, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// buyer
	@ManyToOne
	private UserEntity user;

	@OneToMany(mappedBy = "cart")
	private List<ProductCartEntity> productCartEntities = new ArrayList<>();

	public List<ProductCartEntity> getProductCartEntities() {
		return Collections.unmodifiableList(productCartEntities);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	// preguntar el añadir cantidad
	public Double getTotalOrderPrice() {
		double sum = 0D;
		for (ProductCartEntity productCartEntity : productCartEntities) {
			ProductEntity product = productCartEntity.getProduct();

			if (productCartEntity.getQuantityInCart() == 1) {
				sum += product.getPrice();
			} else if (productCartEntity.getQuantityInCart() > 1) {
				sum += product.getPrice() * productCartEntity.getQuantityInCart();
			}
		}
		return sum;
	}

	public void buy(UserEntity user) throws ProductAlreadySoldException {
		double totalOrderPrice = getTotalOrderPrice();
		user.decMoney(totalOrderPrice);
		this.user = user;
		this.user._getCarts().add(this);

		for (ProductCartEntity productCartEntity : productCartEntities) {
			
			ProductEntity product = productCartEntity.getProduct();
			if(!product.isSold()) {
				product.decStock();
				product.setSold(true);
			}
			else {
                throw new ProductAlreadySoldException("El producto ya ha sido vendido.");
            }
			
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShoppingCartEntity other = (ShoppingCartEntity) obj;
		return  Objects.equals(id, other.id);
	}

	public void incQuantity(ProductEntity prod) {
		for (ProductCartEntity productInCart : productCartEntities) {
	        if (productInCart.getProduct().getProductId().equals(prod.getProductId())) {
	            productInCart.incQuantity(prod);
	            return;
	        }
	    }
		productCartEntities.add(new ProductCartEntity(prod, this));
	}

	
	public void decQuantity(ProductEntity prod) {
		ProductCartEntity toDelete = null;
	    for (ProductCartEntity productInCart : productCartEntities) {
	        if (productInCart.getProduct().getProductId().equals(prod.getProductId())) {
	            if (productInCart.getQuantityInCart() > 0) {
	            	productInCart.decQuantity();
	            	if (productInCart.getQuantityInCart() == 0) toDelete = productInCart;
	                break;
	            }
	        }
	    }
	    if (toDelete != null) productCartEntities.remove(toDelete);
	}
}
