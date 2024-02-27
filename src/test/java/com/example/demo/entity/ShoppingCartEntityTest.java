package com.example.demo.entity;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.entity.ProductCartEntity;
import com.entity.ProductEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.exception.InvalidStockException;
import com.exception.NullDataException;
import com.exception.ProductAlreadySoldException;
import com.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartEntityTest {
	
	@Mock
	private ProductRepository productRepository;
	
	@InjectMocks
    private ShoppingCartEntity shoppingCart;
	
	
	@Test
    void shoppingCartCreation_EqualsAndHashCodeValidation() {
        ProductEntity product1 = new ProductEntity();
        product1.setProductId(1L);

        UserEntity user1 = new UserEntity();
        user1.setUserId(1L);

        LocalDate dateCreated = LocalDate.now();

        ShoppingCartEntity shoppingCart1 = new ShoppingCartEntity();
        shoppingCart1.setId(1L);
        shoppingCart1.setDateCreated(dateCreated);
        shoppingCart1.setUser(user1);

       
        ShoppingCartEntity shoppingCart2 = new ShoppingCartEntity();
        shoppingCart2.setId(1L);
        shoppingCart2.setDateCreated(dateCreated);
        shoppingCart2.setUser(user1);

        Assertions.assertEquals(shoppingCart1, shoppingCart2);
        Assertions.assertEquals(shoppingCart1.hashCode(), shoppingCart2.hashCode());
    }

    @Test
    void shoppingCartTotalOrderPrice_CorrectCalculation() {
       
        ProductEntity product1 = new ProductEntity();
        product1.setProductId(1L);
        product1.setPrice(10.0);

        ProductCartEntity productCart1 = new ProductCartEntity(product1, null);
        productCart1.setQuantityInCart(2);

        ShoppingCartEntity shoppingCart = new ShoppingCartEntity();
        shoppingCart.addProductCart(productCart1);

        Assertions.assertEquals(20.0, shoppingCart.getTotalOrderPrice());
    }

    @Test
    void shoppingCartBuy_ProductAlreadySold_ThrowsException() {
    	   ProductEntity product1 = new ProductEntity();
    	    product1.setProductId(1L);
    	    product1.setStock(0);
    	    product1.setPrice(10.0);  
    	    ProductCartEntity productCart1 = new ProductCartEntity(product1, null);
    	    productCart1.setQuantityInCart(1);

    	    ShoppingCartEntity shoppingCart = new ShoppingCartEntity();
    	    shoppingCart.addProductCart(productCart1);

    	    UserEntity user = new UserEntity();
    	    user.setMoney(100.0);
    	    List<ShoppingCartEntity> carts = new ArrayList<>();
			user.setCarts(carts);

    	   Assertions.assertThrows(ProductAlreadySoldException.class, () -> {
    	        shoppingCart.buy(user, productRepository);
    	    });
    }
    @Test
    void shoppingCartIncQuantity_ProductAlreadyInCart_IncreasesQuantity() {
        ProductEntity product1 = new ProductEntity();
        product1.setProductId(1L);
        product1.setStock(5);
        product1.setPrice(10.0);

        ProductCartEntity existingProductCart = new ProductCartEntity(product1, shoppingCart);
        existingProductCart.setQuantityInCart(1);

        shoppingCart.setProductCartEntities(Collections.singletonList(existingProductCart));

        try {
            shoppingCart.incQuantity(product1);
            Assertions.assertEquals(2, existingProductCart.getQuantityInCart());
        } catch (InvalidStockException e) {
            Assertions.fail("Unexpected exception: " + e.getMessage());
        }
    }
    
    @Test
    void shoppingCartDecQuantity_ToDeleteNotNull_RemovesProductCart() {
    	ProductEntity product1 = new ProductEntity();
        product1.setProductId(1L);

        UserEntity user1 = new UserEntity();
        user1.setUserId(1L);

        LocalDate dateCreated = LocalDate.now();

        ShoppingCartEntity shoppingCart = new ShoppingCartEntity();
        shoppingCart.setId(1L);
        shoppingCart.setDateCreated(dateCreated);
        shoppingCart.setUser(user1);

        ProductCartEntity productCart = new ProductCartEntity(product1, shoppingCart);
        productCart.setQuantityInCart(0);
        shoppingCart.addProductCart(productCart);
       
        Assertions.assertDoesNotThrow(() -> productCart.decQuantity());
        }

    @Test
    void shoppingCartDecQuantity_ProductAlreadyInCart_DecreasesQuantity() throws NullDataException {
        ProductEntity product1 = new ProductEntity();
        product1.setProductId(1L);
        product1.setStock(5);
        product1.setPrice(10.0);

        ProductCartEntity existingProductCart = new ProductCartEntity(product1, shoppingCart);
        existingProductCart.setQuantityInCart(2);

        shoppingCart.setProductCartEntities(Collections.singletonList(existingProductCart));

        shoppingCart.decQuantity(product1);
        Assertions.assertEquals(1, existingProductCart.getQuantityInCart());
    }

    @Test
    void shoppingCartAddProductCart_NewProduct_AddsToCart() {
        ProductEntity product1 = new ProductEntity();
        product1.setProductId(1L);
        product1.setStock(5);
        product1.setPrice(10.0);

        ProductCartEntity newProductCart = new ProductCartEntity(product1, shoppingCart);

        shoppingCart.addProductCart(newProductCart);

        Assertions.assertTrue(shoppingCart.getProductCartEntities().contains(newProductCart));
    }

    @Test
    void shoppingCartRemoveProductCart_RemovesFromCart() throws NullDataException {
        ProductEntity product1 = new ProductEntity();
        product1.setProductId(1L);
        product1.setStock(5);
        product1.setPrice(10.0);

        ProductCartEntity existingProductCart = new ProductCartEntity(product1, shoppingCart);

        shoppingCart.setProductCartEntities(Collections.singletonList(existingProductCart));

        shoppingCart.removeProductCart(existingProductCart);

        Assertions.assertTrue(shoppingCart.getProductCartEntities().isEmpty());
    }

    @Test
    void shoppingCartClear_CartNotEmpty_ClearsCart() {
        ProductEntity product1 = new ProductEntity();
        product1.setProductId(1L);
        product1.setStock(5);
        product1.setPrice(10.0);

        ProductCartEntity existingProductCart = new ProductCartEntity(product1, shoppingCart);

        shoppingCart.setProductCartEntities(Collections.singletonList(existingProductCart));

        shoppingCart.clear();

        Assertions.assertTrue(shoppingCart.getProductCartEntities().isEmpty());
    }

    @Test
    void shoppingCartClear_CartEmpty() {
        shoppingCart.clear();

        Assertions.assertTrue(shoppingCart.getProductCartEntities().isEmpty());
    }
    
    @Test
    void buy_StockMayor0() throws ProductAlreadySoldException {
       
        UserEntity user = new UserEntity();
        user.setMoney(15.0);
        List<ShoppingCartEntity> carts = new ArrayList<>();
        user.setCarts( carts);
        ProductRepository productRepository = mock(ProductRepository.class);
        
       
        ProductEntity product = new ProductEntity();
        product.setStock(5);
        product.setPrice(10.0);
        
        
        ProductCartEntity cartItem = new ProductCartEntity();
        cartItem.setProduct(product);
      
        List<ProductCartEntity> productCartEntities = new ArrayList<>();
        productCartEntities.add(cartItem);
       
        shoppingCart.setProductCartEntities(productCartEntities);
        
        
        List<ProductCartEntity> result = shoppingCart.buy(user, productRepository);
       
        assertEquals(productCartEntities, result);
    }
    
    @Test
    void testGettersAndSetters() {
        Long expectedId = 123L;
        UserEntity user = mock(UserEntity.class);
        LocalDate dateCreated = LocalDate.of(2022, 2, 28);
        
      
        shoppingCart.setId(expectedId);
        shoppingCart.setUser(user);
        shoppingCart.setDateCreated(dateCreated);
        
        assertEquals(expectedId, shoppingCart.getId());
        assertEquals(user, shoppingCart.getUser());
        assertEquals(dateCreated, shoppingCart.getDateCreated());
    }
    
    @Test
    void testIncQuantity_ProductCartEntitiesIsNull() throws InvalidStockException {
       
       ProductEntity product = mock(ProductEntity.class);
        Assertions.assertDoesNotThrow(() -> shoppingCart.incQuantity(product));
        assertEquals(1, shoppingCart.getProductCartEntities().size());
    }
    
   
  
}
