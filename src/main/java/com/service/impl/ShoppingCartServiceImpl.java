package com.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.entity.ProductEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.exception.NotEnoughMoney;
import com.repository.ShoppingCartRepository;
import com.repository.UserRepository;
import com.service.ShoppingCartService;


@Service
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.INTERFACES)
public class ShoppingCartServiceImpl implements ShoppingCartService {

	@Autowired
	private ShoppingCartRepository shoppingCartRepo;
	
	@Autowired
	private UserRepository userRepository;

	private ShoppingCartEntity cart = new ShoppingCartEntity();


	public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepo, ShoppingCartEntity cart) {
		super();
		this.shoppingCartRepo = shoppingCartRepo;
		this.cart = cart;
	}

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
	@Transactional
	public void buyShoppingCart(String userName) throws NotEnoughMoney {
		UserEntity user = userRepository.findByUsername(userName);
		if(user.getMoney()>=cart.getTotalOrderPrice()) {
			cart.buy(user);
			shoppingCartRepo.save(cart);
		}
		else {
			throw new NotEnoughMoney("User doesn't have enough money");
		}		
	}

	
	

	

}
