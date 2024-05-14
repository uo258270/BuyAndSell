package com.tfg.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.tfg.entity.ProductCartEntity;
import com.tfg.entity.ProductEntity;
import com.tfg.entity.ShoppingCartEntity;
import com.tfg.entity.UserEntity;
import com.tfg.entity.enums.RoleEnum;
import com.tfg.exception.InvalidStockException;
import com.tfg.exception.NotEnoughMoney;
import com.tfg.exception.NullDataException;
import com.tfg.exception.ProductAlreadySoldException;
import com.tfg.repository.ProductCartRepository;
import com.tfg.repository.ProductRepository;
import com.tfg.repository.ShoppingCartRepository;
import com.tfg.repository.UserRepository;
import com.tfg.service.impl.ShoppingCartServiceImpl;

import jakarta.transaction.Transactional;

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
	private ProductCartRepository productCartRepository;
	@Mock
	private UserRepository userRepo;

	private ShoppingCartEntity cart = new ShoppingCartEntity();

	@BeforeEach
	public void init() {
		MockitoAnnotations.openMocks(this);
		cartService = new ShoppingCartServiceImpl(cartRepo, userRepo, productRepository, productCartRepository);
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
		
		carts.add(cart);
		user.setCarts(carts);
		Mockito.when(userRepo.findByEmail(any())).thenReturn(user);
		
		cartService.setCart(cart);
		Mockito.when(cartRepo.save(any())).thenReturn(cart);
		ShoppingCartEntity result = cartService.buyShoppingCart("1");

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
		cartService.setCart(cart);

		Assertions.assertThrows(NotEnoughMoney.class, () -> cartService.buyShoppingCart(any()));
		Mockito.verify(cartRepo, Mockito.never()).save(Mockito.any(ShoppingCartEntity.class));
	}

	@Test
	void incrementProductQuantity_ValidStock() throws InvalidStockException {
		Long productId = 1L;
		ProductEntity product = createMockedProduct();
		product.setStock(5);
		

		when(productRepository.findByProductId(eq(productId))).thenReturn(product);

		ShoppingCartEntity updatedCart = cartService.incrementProductQuantity(productId, 1);

		assertEquals(1, updatedCart.getProductCartEntities().get(0).getQuantityInCart());
		verify(productRepository, Mockito.times(1)).findByProductId(eq(productId));
	}

	@Test
	void incrementProductQuantity_InvalidStockException() {
		Long productId = 1L;
		ProductEntity product = createMockedProduct();
		product.setStock(0);
		Mockito.when(productRepository.findByProductId(productId)).thenReturn(product);

		Assertions.assertThrows(InvalidStockException.class, () -> cartService.incrementProductQuantity(productId, 1));
	}

	@Test
	void decrementProductQuantity() throws NullDataException {
		UserEntity user = createMockedUser();
		ProductEntity product = createMockedProduct();
		List<ProductCartEntity> prods = new ArrayList<>();
		ProductCartEntity pro = new ProductCartEntity();
		pro.setId(1L);
		pro.setCart(cart);
		pro.setProduct(product);
		pro.setQuantityInCart(4);
		prods.add(pro);

		cart.setProductCartEntities(prods);
		user.setCarts(Collections.singletonList(cart));

		when(productRepository.findByProductId(any())).thenReturn(product);
		cartService.setCart(cart);
		ShoppingCartEntity updatedCart = cartService.decrementProductQuantity(1L, 1);

		assertEquals(3, updatedCart.getProductCartEntities().get(0).getQuantityInCart());
		verify(productRepository, Mockito.times(1)).findByProductId(eq(1L));
	}

	@Test
	void decrementProductQuantity0() throws NullDataException {
		UserEntity user = createMockedUser();
		ProductEntity product = createMockedProduct();
		List<ProductCartEntity> prods = new ArrayList<>();
		ProductCartEntity pro = new ProductCartEntity();
		pro.setId(1L);
		pro.setCart(cart);
		pro.setProduct(product);
		pro.setQuantityInCart(1);
		prods.add(pro);

		cart.setProductCartEntities(prods);
		user.setCarts(Collections.singletonList(cart));

		when(productRepository.findByProductId(any())).thenReturn(product);

		ShoppingCartEntity updatedCart = cartService.decrementProductQuantity(1L, 1);

		assertEquals(0, updatedCart.getProductCartEntities().size());
		verify(productRepository, Mockito.times(1)).findByProductId(eq(1L));
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

	
	
	@Test
	void getCart_ReturnsCart() {
	    ShoppingCartEntity existingCart = new ShoppingCartEntity();
	    existingCart.setId(1L);
	    cartService.setCart(existingCart);
	    when(cartRepo.getById(any())).thenReturn(existingCart);
	    ShoppingCartEntity result = cartService.getCart();
	    Assertions.assertNotNull(result);
	    Assertions.assertEquals(existingCart, result);
	}
	
	@Test
	void getCart_InitializesProductCartEntities() {
	    ShoppingCartEntity existingCart = new ShoppingCartEntity();
	    existingCart.setId(1L);
	    cartService.setCart(existingCart);
	  
	    List<ProductCartEntity> productCartEntities = new ArrayList<>();
	    productCartEntities.add(new ProductCartEntity());
	    existingCart.setProductCartEntities(productCartEntities);
	    
	  
	    ShoppingCartEntity result = cartService.getCart();

	    Assertions.assertNotNull(result);
	    Assertions.assertEquals(existingCart, result);
	}



	@Test
	void getCart_NoProductsInExistingCart() {
	    ShoppingCartEntity existingCart = new ShoppingCartEntity();
	    existingCart.setId(1L);
	    cartService.setCart(existingCart);
	   
	    ShoppingCartEntity result = cartService.getCart();
	    Assertions.assertNotNull(result);
	}

	@Test
	void getCart_NoExistingCart_ReturnsNewCart() {
	    ShoppingCartEntity result = cartService.getCart();

	    Assertions.assertNotNull(result);
	    Assertions.assertTrue(result.getProductCartEntities().isEmpty());
	}
	
	@Test
	void decQuantity_NullDataExceptionThrown() throws NullDataException {
	    Long productId = 1L;
	    ProductEntity pro = new ProductEntity();
	    ShoppingCartEntity cart = Mockito.mock(ShoppingCartEntity.class); // Crear un mock para cart
	    when(productRepository.findByProductId(productId)).thenReturn(pro);

	    Mockito.doThrow(new NullDataException("cannot be decremented")).when(cart).decQuantity(any(), any());

	    cartService.setCart(cart);
	    
	    assertThrows(NullDataException.class, () -> cartService.decrementProductQuantity(productId, 1));
	}


	

}
