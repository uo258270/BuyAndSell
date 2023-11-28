package com.service.impl;

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
import com.exception.NotEnoughMoney;
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
	
	private ProductCartRepository productCartRepository;

	private ShoppingCartEntity cart = new ShoppingCartEntity();

	public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepo, ShoppingCartEntity cart) {
		super();
		this.shoppingCartRepo = shoppingCartRepo;
		this.cart = cart;
	}

	@Override
	public void clear(ShoppingCartEntity cart) {
		cart.getProductCartEntities().clear();
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
		if (cart != null && cart.getProductCartEntities() != null) {
	        List<ProductCartEntity> productCartEntities = cart.getProductCartEntities();

	        for (ProductCartEntity pc : productCartEntities) {
	            if (pc.getProduct().equals(product)) {
	                productCartEntities.remove(pc);
	                return; 
	            }
	        }

	        throw new RuntimeException("Product not found in this cart");
	    } else {
	        throw new RuntimeException("There are no products in this cart");
	    }

	}

	//preguntar esto
	@Override
	public void addProduct(ProductEntity product, int quantity) {
		 ProductCartEntity existingProductCart = findProductInCart(cart, product);

        if (existingProductCart != null) {
            existingProductCart.incQuantity(product);
            productCartRepository.save(existingProductCart);
        } else {
            ProductCartEntity newProductCart = new ProductCartEntity();
            newProductCart.setCart(cart);
            newProductCart.setProduct(product);
            newProductCart.setQuantityInCart(quantity);
            productCartRepository.save(newProductCart);
            
            cart.getProductCartEntities().add(newProductCart);
            shoppingCartRepo.save(cart);
        }
	}
	
	private ProductCartEntity findProductInCart(ShoppingCartEntity cart, ProductEntity product) {
	    List<ProductCartEntity> productCartEntities = cart.getProductCartEntities();

	    for (ProductCartEntity pc : productCartEntities) {
	        if (pc.getProduct().equals(product)) {
	            return pc;
	        }
	    }

	    return null;
	}

	@Override
	public Optional<ShoppingCartEntity> getCart() {
		return Optional.ofNullable(cart);
	}

	@Override
	@Transactional
	public void buyShoppingCart(String userName) throws NotEnoughMoney {
		UserEntity user = userRepository.findByUsername(userName);
		if (user.getMoney() >= cart.getTotalOrderPrice()) {
			cart.buy(user);
			shoppingCartRepo.save(cart);
			for (ProductCartEntity product : cart.getProductCartEntities()) {
				product.setQuantityInCart(0);
			}
		} else {
			throw new NotEnoughMoney("User doesn't have enough money");
		}
	}
	
	
	@Override
	public void incrementProductQuantity(Long productId) {
		ProductEntity prod = productRepository.findByProductId(productId);
		 for (ProductCartEntity productInCart : cart.getProductCartEntities()) {
		        if (productInCart.getProduct().getProductId().equals(prod.getProductId())) {
		            productInCart.incQuantity(prod);
		            productRepository.save(prod);
		            break;
		        }
		    }

	}

	@Override
	public void decrementProductQuantity(Long productId) {
		ProductEntity prod = productRepository.findByProductId(productId);
	    for (ProductCartEntity productInCart : cart.getProductCartEntities()) {
	        if (productInCart.getProduct().getProductId().equals(prod.getProductId())) {
	            if (productInCart.getQuantityInCart() > 0) {
	            	productInCart.decQuantity(); 
	                productRepository.save(prod);  
	                break;
	            }
	        }
	    }

	}

}
