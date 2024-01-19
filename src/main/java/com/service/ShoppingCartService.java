package com.service;

import java.util.List;
import java.util.Optional;

import com.entity.ProductEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.exception.InvalidStockException;
import com.exception.NotEnoughMoney;
import com.exception.ProductAlreadySoldException;

public interface ShoppingCartService {

	//findByUser
	//add
	//delete
	//updateUnits
	//clear

	void clear();

	Optional<ShoppingCartEntity> getCart();


	List<ShoppingCartEntity> findShoppingsByUser(UserEntity user);

	 void buyShoppingCart(String name) throws NotEnoughMoney, ProductAlreadySoldException;

	void incrementProductQuantity(Long productId) throws InvalidStockException;

	void decrementProductQuantity(Long productId);

	
	
	
	
}
