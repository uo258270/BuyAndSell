package com.service;

import java.util.List;
import java.util.Optional;

import com.entity.ProductEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;

public interface ShoppingCartService {

	//findByUser
	//add
	//delete
	//updateUnits
	//clear

	void clear(ShoppingCartEntity cart);

	Optional<ShoppingCartEntity> getCart();

	void addProduct(ProductEntity product);

	List<ShoppingCartEntity> findShoppingsByUser(UserEntity user);

	void deleteProductFromShoppingCart(ProductEntity product);

	Double getTotalOrderPrice();

	void buyShoppingCart();


	
	
	
}
