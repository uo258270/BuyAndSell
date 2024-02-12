package com.service;

import java.util.List;

import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.exception.InvalidStockException;
import com.exception.NotEnoughMoney;
import com.exception.ProductAlreadySoldException;

public interface ShoppingCartService {

	ShoppingCartEntity clear();

	ShoppingCartEntity getCart();


	List<ShoppingCartEntity> findShoppingsByUser(UserEntity user);

	 ShoppingCartEntity buyShoppingCart(String name) throws NotEnoughMoney, ProductAlreadySoldException;

	ShoppingCartEntity incrementProductQuantity(Long productId) throws InvalidStockException;

	ShoppingCartEntity decrementProductQuantity(Long productId);

	
	
	
	
}
