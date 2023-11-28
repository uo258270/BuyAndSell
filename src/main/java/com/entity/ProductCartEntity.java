package com.entity;


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

	public void incQuantity(ProductEntity prod) {
		int stock = prod.getStock();
		if((quantityInCart+1)<=stock) {
			quantityInCart += 1;
		}else {
			throw new IllegalArgumentException();
		}
		
	}
	
	public void decQuantity() {
		if((quantityInCart-1)>0) {
			quantityInCart -= 1;
		}else {
			throw new IllegalArgumentException();
		}
		
	}
	
	
	
}
