package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.entity.ProductCartEntity;
import com.entity.ProductEntity;
import com.entity.ShoppingCartEntity;
import com.repository.ProductRepository;
import com.repository.ShoppingCartRepository;
import com.repository.UserRepository;
import com.service.impl.ShoppingCartServiceImpl;

import jakarta.transaction.Transactional;

import java.util.Date;
import org.junit.jupiter.api.Assertions;

import static org.mockito.ArgumentMatchers.any;
import com.entity.UserEntity;
import com.entity.enums.RoleEnum;
import com.exception.InvalidStockException;
import com.exception.NotEnoughMoney;
import com.exception.ProductAlreadySoldException;

@ExtendWith(MockitoExtension.class)
@Transactional
public class ShoppingCartServiceImplTest {
	
	@InjectMocks
	private ShoppingCartServiceImpl cartService;

	@Mock
	private ProductRepository productRepository;
	@Mock
	private ShoppingCartRepository cartRepo;

	@Mock
	private UserRepository userRepo;

	
	private ShoppingCartEntity cart = new ShoppingCartEntity();

	

	@BeforeEach
	public void init() {
		MockitoAnnotations.openMocks(this);
		cartService = new ShoppingCartServiceImpl(cartRepo, userRepo, productRepository, cart);
	}

	 @Test
	    void findShoppingsByUser_OK() {
	        UserEntity user = createMockedUser();
	        List<ShoppingCartEntity> carts = new ArrayList<>();
	        carts.add(new ShoppingCartEntity());
	        Mockito.when(cartRepo.getByUserId(user.getUserId())).thenReturn(carts);

	        List<ShoppingCartEntity> result = cartService.findShoppingsByUser(user);

	        Assertions.assertNotNull(result);
	        Assertions.assertEquals(carts, result);
	    }

	    @Test
	    void findShoppingsByUser_ThrowsRuntimeException() {
	        UserEntity user = createMockedUser();
	        Mockito.when(cartRepo.getByUserId(user.getUserId())).thenReturn(new ArrayList<>());

	        Assertions.assertThrows(RuntimeException.class, () -> cartService.findShoppingsByUser(user));
	    }


	    @Test
	    void buyShoppingCart_EnoughMoney_buys() throws NotEnoughMoney, ProductAlreadySoldException {
	       
	        UserEntity user = createMockedUser();
	        ProductEntity product = createMockedProduct();
	        cart.setId(1L);
	        List<ProductCartEntity> prods = new ArrayList<>();
	        ProductCartEntity pro = new ProductCartEntity();
	        pro.setId(1L);
	        pro.setProduct(product);
	        pro.setQuantityInCart(1);
			prods.add(pro);
			cart.setProductCartEntities(prods);
			List<ShoppingCartEntity> carts = new ArrayList<>();
			ShoppingCartEntity cart1= new ShoppingCartEntity();
			carts.add(cart1);
			user.setCarts(carts);
	        Mockito.when(userRepo.findByEmail(any())).thenReturn(user);
	        
	       ShoppingCartEntity result =  cartService.buyShoppingCart(any());

	        Mockito.verify(cartRepo, Mockito.times(1)).save(Mockito.any(ShoppingCartEntity.class));
	        Assertions.assertTrue(result.getProductCartEntities().isEmpty());
	    }

	    @Test
	    void buyShoppingCart_ThrowsNotEnoughMoneyException() {
	        ProductEntity product = createMockedProduct();
	        cart.setId(1L);
	        List<ProductCartEntity> prods = new ArrayList<>();
	        ProductCartEntity pro = new ProductCartEntity();
	        pro.setId(1L);
	        pro.setProduct(product);
	        pro.setQuantityInCart(1);
			prods.add(pro);
			cart.setProductCartEntities(prods);
	        UserEntity user = createMockedUser();
	        
	        user.setMoney(0.0);
	        Mockito.when(userRepo.findByEmail(any())).thenReturn(user);

	        Assertions.assertThrows(NotEnoughMoney.class, () -> cartService.buyShoppingCart(any()));
	        Mockito.verify(cartRepo, Mockito.never()).save(Mockito.any(ShoppingCartEntity.class));
	    }

	    @Test
	    void incrementProductQuantity_IncQuantity() throws InvalidStockException {
	        Long productId = 1L;
	        ProductEntity product = createMockedProduct();
	        cart.setId(1L);
	        List<ProductCartEntity> prods = new ArrayList<>();
	        ProductCartEntity pro = new ProductCartEntity();
	        pro.setId(1L);
	        pro.setProduct(product);
	        pro.setQuantityInCart(1);
			prods.add(pro);
			cart.setProductCartEntities(prods);
	        Mockito.when(productRepository.findByProductId(any())).thenReturn(product);

	        Assertions.assertEquals(cart.getProductCartEntities().get(0).getQuantityInCart(), 1);
	        
	        ShoppingCartEntity carts = cartService.incrementProductQuantity(productId);

	        Assertions.assertEquals(carts.getProductCartEntities().get(0).getQuantityInCart(), 2);
	    }

	    @Test
	    void incrementProductQuantity_InvalidStockException() {
	        Long productId = 1L;
	        ProductEntity product = createMockedProduct();
	        product.setStock(0);
	        Mockito.when(productRepository.findByProductId(productId)).thenReturn(product);

	        Assertions.assertThrows(InvalidStockException.class, () -> cartService.incrementProductQuantity(productId));
	    }

	    @Test
	    void decrementProductQuantity_DecQuantity() {
	        Long productId = 1L;
	        ProductEntity product = createMockedProduct();
	        cart.setId(1L);
	        List<ProductCartEntity> prods = new ArrayList<>();
	        ProductCartEntity pro = new ProductCartEntity();
	        pro.setId(1L);
	        pro.setProduct(product);
	        pro.setQuantityInCart(2);
			prods.add(pro);
			cart.setProductCartEntities(prods);
	        Mockito.when(productRepository.findByProductId(any())).thenReturn(product);

	        Assertions.assertEquals(cart.getProductCartEntities().get(0).getQuantityInCart(), 2);
	        
	        ShoppingCartEntity carts = cartService.decrementProductQuantity(productId);

	        Assertions.assertEquals(1, carts.getProductCartEntities().get(0).getQuantityInCart());
	    }

	    private UserEntity createMockedUser() {
	    	UserEntity user = new UserEntity();
			user.setUserId(1L);
			user.setEmail("1");
			user.setLastName("1");
			user.setMoney(10.0);
			user.setName("1");
			user.setPassword("1");
			user.setPasswordConfirm("1");
			user.setRegisterDate(new Date());
			user.setRole(RoleEnum.ROLE_ADMIN);
			user.setUsername("1");
			return user;
	    }

	    @Test
	    void clear_OK() {
	        
	        ShoppingCartEntity cart = new ShoppingCartEntity();
	        List<ProductCartEntity> initialProducts = new ArrayList<>();
	        initialProducts.add(new ProductCartEntity());
	        ReflectionTestUtils.setField(cart, "productCartEntities", initialProducts);
	        ShoppingCartEntity carts = cartService.clear();

	        Assertions.assertTrue(carts.getProductCartEntities().isEmpty());
	        Assertions.assertEquals(carts.getTotalOrderPrice(), 0);
	    }
	    
	    
	    private ProductEntity createMockedProduct() {
	        ProductEntity product = new ProductEntity();
	        product.setProductId(1L);
	        product.setName("Test Product");
	        product.setDetail("Product description");
	        product.setPrice(10.0);
	        product.setStock(3);
	       
	        return product;
	    }
	

}
