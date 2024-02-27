package com.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.entity.ProductCartEntity;
import com.entity.ProductEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.exception.InvalidStockException;
import com.exception.NotEnoughMoney;
import com.exception.NullDataException;
import com.exception.ProductAlreadySoldException;
import com.repository.ProductRepository;
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

	@Autowired
	private ProductRepository productRepository;

	private ShoppingCartEntity cart = new ShoppingCartEntity();

	
	
	public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepo, UserRepository userRepository,
			ProductRepository productRepository) {
		super();
		this.shoppingCartRepo = shoppingCartRepo;
		this.userRepository = userRepository;
		this.productRepository = productRepository;
		
	}
	
	 public void setCart(ShoppingCartEntity cart) {
	        this.cart = cart;
	    }


	@Override
	@Transactional
	public ShoppingCartEntity clear() {
		cart.clear();
		shoppingCartRepo.save(cart);

		return cart;
	}

	@Override
	public List<ShoppingCartEntity> findShoppingsByUser(UserEntity user) {
		List<ShoppingCartEntity> carts = shoppingCartRepo.getByUserId(user.getUserId());
		if (carts != null && !carts.isEmpty()) {
			return carts;
		} else {
			throw new RuntimeException("this user does not have any shoppings made");
		}
	}

	@Override
	public ShoppingCartEntity getCart() {
	    if (cart == null || cart.getProductCartEntities() == null || cart.getProductCartEntities().isEmpty()) {
	        if (cart != null) {
	            cart = shoppingCartRepo.getById(cart.getId());
	        }
	        if (cart == null) {
	            cart = new ShoppingCartEntity();
	            cart.setProductCartEntities(new ArrayList<>());
	        }
	    } else {
	         Hibernate.initialize(cart.getProductCartEntities());
	    }
	    return cart;
	}

	@Override
	@Transactional
	public ShoppingCartEntity buyShoppingCart(String email) throws NotEnoughMoney, ProductAlreadySoldException {
		UserEntity user = userRepository.findByEmail(email);
		if (user.getMoney() >= cart.getTotalOrderPrice()) {
			List<ProductCartEntity> products = cart.buy(user, productRepository);
			cart.setProductCartEntities(products);
			cart = shoppingCartRepo.save(cart);
			 cart = new ShoppingCartEntity();
		} else {
			throw new NotEnoughMoney("El usuario no tiene suficiente dinero para realizar la compra");
		}
		return cart;
	}

	@Override
	public ShoppingCartEntity incrementProductQuantity(Long productId) throws InvalidStockException {
		ProductEntity prod = productRepository.findByProductId(productId);
		if (prod.getStock() < 1) {
			throw new InvalidStockException("No hay stock suficiente de este producto");
		}
		cart.incQuantity(prod);
		return cart;

	}

	@Override
	public ShoppingCartEntity decrementProductQuantity(Long productId) throws NullDataException {
		ProductEntity prod = productRepository.findByProductId(productId);
		try {
			cart.decQuantity(prod);
		} catch (NullDataException e) {
			 throw new NullDataException("cannot be decremented");
		}
		return cart;
	}

	
}
