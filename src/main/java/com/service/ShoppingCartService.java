package com.service;

import java.util.List;
import java.util.Optional;

import com.entity.ProductEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.exception.NotEnoughMoney;

public interface ShoppingCartService {

	//findByUser
	//add
	//delete
	//updateUnits
	//clear

	void clear(ShoppingCartEntity cart);

	Optional<ShoppingCartEntity> getCart();


	List<ShoppingCartEntity> findShoppingsByUser(UserEntity user);

	void deleteProductFromShoppingCart(ProductEntity product);

	
	 void buyShoppingCart(String name) throws NotEnoughMoney;

	void incrementProductQuantity(Long productId);

	void decrementProductQuantity(Long productId);

	void addProduct(ProductEntity product, int quantity);

	
	
	
}
