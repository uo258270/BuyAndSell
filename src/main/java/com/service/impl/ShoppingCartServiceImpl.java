package com.service.impl;

import java.time.LocalDate;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.entity.ProductEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.repository.ProductRepository;
import com.repository.ShoppingCartRepository;
import com.service.ShoppingCartService;

import jakarta.persistence.Transient;

@Service
@Scope("session")
public class ShoppingCartServiceImpl implements ShoppingCartService{
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ShoppingCartRepository shoppingCartRepo;
	
	private ShoppingCartEntity cart;
	
    @Override
    public Iterable<ShoppingCartEntity> getAllOrders() {
    	List<ShoppingCartEntity> orders = shoppingCartRepo.findAll();
    	return orders;
    }
	
    @Override
    public void create(ShoppingCartEntity order) {
        order.setDateCreated(LocalDate.now());
        shoppingCartRepo.save(order);
    }

    @Override
    public void update(ShoppingCartEntity order) {
        shoppingCartRepo.save(order);
    }

    @Transient
    public Double getTotalOrderPrice() {
        double sum = 0D;
        List<ProductEntity> products = productRepository.findAll();
        for (ProductEntity op : products) {
            sum += op.getPrice();
        }
        return sum;
    }
	
	@Override
	public void clear() {
		cart = new ShoppingCartEntity();
	}


	@Override
	public  List<ShoppingCartEntity> findByUser(UserEntity user) {
		 List<ShoppingCartEntity> carts = shoppingCartRepo.getByUserId(user.getUserId());
		 if(carts!=null && !carts.isEmpty()){
			 return carts;
		 }else {
			 throw new RuntimeException("this user doues not have any shoppings made");
		 }
	}

//no estoy segura
	@Override
	public void deleteProductFromShoppingCart(ProductEntity product, ShoppingCartEntity cart) {
		ShoppingCartEntity sho = shoppingCartRepo.getById(cart.getId());
		if(sho!=null) {
			List<ProductEntity> productos = sho.getProducts();
			if(productos!=null && !productos.isEmpty()) {
				productos.remove(product);
			}
			else {
				throw new RuntimeException("There are no products in this cart");
			}
		}
		
	}

//mirar
	@Override
	public void addProduct(ProductEntity product, ShoppingCartEntity cart) {
		
		ShoppingCartEntity sho = shoppingCartRepo.getById(cart.getId());
		if(sho!=null) {
			List<ProductEntity> productos = sho.getProducts();
			productos.add(product);
		}
		else {
			throw new RuntimeException("cart does not exist on the data base");
		}
	}



}
