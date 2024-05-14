package com.tfg.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.entity.ProductCartEntity;
import com.tfg.entity.ProductEntity;
import com.tfg.entity.ShoppingCartEntity;
import com.tfg.entity.UserEntity;
import com.tfg.exception.InvalidStockException;
import com.tfg.exception.NotEnoughMoney;
import com.tfg.exception.NullDataException;
import com.tfg.exception.ProductAlreadySoldException;
import com.tfg.repository.ProductCartRepository;
import com.tfg.repository.ProductRepository;
import com.tfg.repository.ShoppingCartRepository;
import com.tfg.repository.UserRepository;
import com.tfg.service.ShoppingCartService;

@Service
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.INTERFACES)
public class ShoppingCartServiceImpl implements ShoppingCartService {

	@Autowired
	private ShoppingCartRepository shoppingCartRepo;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ProductCartRepository productCartRepository;
	

	private ShoppingCartEntity cart = new ShoppingCartEntity();

	
	
	public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepo, UserRepository userRepository,
			ProductRepository productRepository, ProductCartRepository productCartRepository) {
		super();
		this.shoppingCartRepo = shoppingCartRepo;
		this.userRepository = userRepository;
		this.productRepository = productRepository;
		this.productCartRepository  = productCartRepository;
		
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
			productCartRepository.saveAll(products);
			cart.setProductCartEntities(products);
			cart = shoppingCartRepo.save(cart);
			 cart.clear();
		} else {
			throw new NotEnoughMoney("El usuario no tiene suficiente dinero para realizar la compra");
		}
		return cart;
	}

	@Override
	public ShoppingCartEntity incrementProductQuantity(Long productId, Integer quantity) throws InvalidStockException {
		ProductEntity prod = productRepository.findByProductId(productId);
		Integer quantityInCart = cart.getQuantityInCart(prod);
		if (prod.getStock() < quantity + quantityInCart) {
			throw new InvalidStockException("No hay stock suficiente de este producto");
		}
		cart.incQuantity(prod, quantity);
		return cart;

	}

	@Override
	public ShoppingCartEntity decrementProductQuantity(Long productId, Integer quantity) throws NullDataException {
		ProductEntity prod = productRepository.findByProductId(productId);
		try {
			cart.decQuantity(prod, quantity);
		} catch (NullDataException e) {
			 throw new NullDataException("cannot be decremented");
		}
		return cart;
	}

	
}
