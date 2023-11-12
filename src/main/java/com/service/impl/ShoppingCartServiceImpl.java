package com.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.entity.ProductEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.repository.ShoppingCartRepository;
import com.service.ShoppingCartService;

import jakarta.persistence.Transient;

@Service
@Scope("session")
public class ShoppingCartServiceImpl implements ShoppingCartService {

	@Autowired
	private ShoppingCartRepository shoppingCartRepo;

	private ShoppingCartEntity cart = new ShoppingCartEntity();


	@Override
	public void clear(ShoppingCartEntity cart) {
		cart.getProducts().clear();
	}

	@Override
	public List<ShoppingCartEntity> findShoppingsByUser(UserEntity user) {
		List<ShoppingCartEntity> carts = shoppingCartRepo.getByUserId(user.getUserId());
		if (carts != null && !carts.isEmpty()) {
			return carts;
		} else {
			throw new RuntimeException("this user doues not have any shoppings made");
		}
	}

	@Override
	public void deleteProductFromShoppingCart(ProductEntity product) {
		if (cart != null && cart.getProducts() != null) {
			List<ProductEntity> productos = cart.getProducts();
			if (productos != null && !productos.isEmpty()) {
				productos.remove(product);
			} else {
				throw new RuntimeException("There are no products in this cart");
			}
		}

	}

	@Override
	public void addProduct(ProductEntity product) {
		List<ProductEntity> productos = cart.getProducts();
		productos.add(product);
	}

	@Override
	public Optional<ShoppingCartEntity> getCart() {
		return Optional.ofNullable(cart);
	}

	@Override
	public void buyShoppingCart() {
		Double total = getTotalOrderPrice();
		if(cart.getUser().getMoney()>=total) {
			//restar dinero
			Double initialMoney = cart.getUser().getMoney();
			Double reductedMoney = initialMoney-total;
			cart.getUser().setMoney(reductedMoney);
			//añadir compra al usuario
			List<ShoppingCartEntity> cartsList = cart.getUser().getCarts();
			cartsList.add(cart);
			cart.getUser().setCarts(cartsList);
			for(ProductEntity pro : cart.getProducts()) {
				pro.setStock(pro.getStock()-1);
			}
		}
		
	}
	

	
	@Transient
	@Override
	public Double getTotalOrderPrice() {
		double sum = 0D;
		List<ProductEntity> products = cart.getProducts();
		for (ProductEntity op : products) {
			sum += op.getPrice();
		}
		return sum;
	}

	


}
