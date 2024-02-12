package com.entity;

import java.util.Objects;

import com.exception.InvalidStockException;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ProductCartEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private ProductEntity product;

	@ManyToOne
	private ShoppingCartEntity cart;

	private int quantityInCart;

	public ProductCartEntity() {
	}

	public ProductCartEntity(ProductEntity product, ShoppingCartEntity cart) {
		super();
		this.product = product;
		this.cart = cart;
		quantityInCart = 1;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductEntity getProduct() {
		return product;
	}

	public void setProduct(ProductEntity product) {
		this.product = product;
	}

	public ShoppingCartEntity getCart() {
		return cart;
	}

	public void setCart(ShoppingCartEntity cart) {
		this.cart = cart;
	}

	public int getQuantityInCart() {
		return quantityInCart;
	}

	public void setQuantityInCart(int quantityInCart) {
		this.quantityInCart = quantityInCart;
	}

	public void incQuantity(ProductEntity prod) throws InvalidStockException {
	    int stock = prod.getStock();
	    if ((quantityInCart + 1) <= stock) {
	        quantityInCart += 1;
	    } else {
	        throw new InvalidStockException("No hay suficiente stock disponible para este producto.");
	    }
	}

	public void decQuantity() {
		if ((quantityInCart - 1) > 0) {
			quantityInCart -= 1;
		} else {
			cart.removeProductCart(this);
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
		ProductCartEntity other = (ProductCartEntity) obj;
		return Objects.equals(id, other.id);
	}
	
	

}
