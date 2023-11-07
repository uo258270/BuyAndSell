package com.service;

import java.util.List;

import com.entity.ProductEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;

public interface ShoppingCartService {

	//findByUser
	//add
	//delete
	//updateUnits
	//clear
	
	Iterable<ShoppingCartEntity> getAllOrders();

	void create(ShoppingCartEntity order);

	void update(ShoppingCartEntity order);
	
	void deleteProductFromShoppingCart(ProductEntity product, ShoppingCartEntity cart);
	
	void clear();
	
	List<ShoppingCartEntity> findByUser(UserEntity user);
	
	void addProduct(ProductEntity product, ShoppingCartEntity cart);
	
	
	
}
