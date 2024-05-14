package com.tfg.service;

import java.util.List;

import com.tfg.entity.ShoppingCartEntity;
import com.tfg.entity.UserEntity;
import com.tfg.exception.InvalidStockException;
import com.tfg.exception.NotEnoughMoney;
import com.tfg.exception.NullDataException;
import com.tfg.exception.ProductAlreadySoldException;

public interface ShoppingCartService {

	ShoppingCartEntity clear();

	ShoppingCartEntity getCart();


	List<ShoppingCartEntity> findShoppingsByUser(UserEntity user);

	 ShoppingCartEntity buyShoppingCart(String name) throws NotEnoughMoney, ProductAlreadySoldException;

	ShoppingCartEntity incrementProductQuantity(Long productId, Integer quantity) throws InvalidStockException;

	ShoppingCartEntity decrementProductQuantity(Long productId, Integer quantity) throws NullDataException;

	
	
	
	
}
