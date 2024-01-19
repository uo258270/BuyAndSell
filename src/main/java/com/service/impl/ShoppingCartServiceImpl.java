package com.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.exception.ProductAlreadySoldException;
import com.repository.ProductCartRepository;
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

	@Autowired
	private ProductCartRepository productCartRepository;

	private ShoppingCartEntity cart = new ShoppingCartEntity();

	@Override
	@Transactional
	public void clear() {
		cart.clear();
		shoppingCartRepo.save(cart);

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
	public Optional<ShoppingCartEntity> getCart() {
		return Optional.ofNullable(cart);
	}

	@Override
	@Transactional
	public void buyShoppingCart(String email) throws NotEnoughMoney, ProductAlreadySoldException {
		UserEntity user = userRepository.findByEmail(email);
		if (user.getMoney() >= cart.getTotalOrderPrice()) {
			cart.buy(user, productRepository);
			cart = shoppingCartRepo.save(cart);
			List<ProductCartEntity> list = cart.getProductCartEntities();
			for (ProductCartEntity product : list) {
				product.setQuantityInCart(0);
			}
		} else {
			throw new NotEnoughMoney("El usuario no tiene suficiente dinero para realizar la compra");
		}
	}

	@Override
	public void incrementProductQuantity(Long productId) throws InvalidStockException {
		ProductEntity prod = productRepository.findByProductId(productId);
		if (prod.getStock() < 1) {
			throw new InvalidStockException("No hay stock suficiente de este producto");
		}
		cart.incQuantity(prod);

	}

	@Override
	public void decrementProductQuantity(Long productId) {
		ProductEntity prod = productRepository.findByProductId(productId);
		cart.decQuantity(prod);
	}

}
