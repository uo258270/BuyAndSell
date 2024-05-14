package com.tfg.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.entity.FeaturedProductEntity;
import com.tfg.entity.ProductCartEntity;
import com.tfg.entity.ProductEntity;
import com.tfg.entity.ReviewEntity;
import com.tfg.entity.ShoppingCartEntity;
import com.tfg.entity.UserEntity;
import com.tfg.entity.enums.CategoryEnum;

@ExtendWith(MockitoExtension.class)
public class ProductEntityTest {

	@Test
	void decStock_OK() {
		ProductEntity product = new ProductEntity();
		product.setStock(5);
		product.decStock();
		Assertions.assertEquals(4, product.getStock());
	}

	@Test
	void decStock_exception() {
		ProductEntity product = new ProductEntity();
		product.setStock(0);
		Assertions.assertThrows(IllegalArgumentException.class, product::decStock);
	}

	@Test
	void addReview_AddReviewToProduct_IncreasesReviewCount() {
		ProductEntity product = new ProductEntity();
		ReviewEntity review = new ReviewEntity();
		review.setRatingId(1L);
		review.setRating(4);
		List<ReviewEntity> reviews = new ArrayList<>();
		product.setReviews(reviews);
		product.getReviews().add(review);

		Assertions.assertEquals(1, product.getReviews().size());
	}

	@Test
	void createProd() {
		ProductEntity product = new ProductEntity();
		product.setProductId(1L);
		product.setName("product");
		product.setDetail("detalle");
		product.setProductDate(new Date());
		product.setUpdateDate(new Date());
		product.setPrice(50.0);
		product.setStock(10);
		product.setCategory(CategoryEnum.ELECTRODOMESTICOS);
		product.setNumOfViews(100);
		product.setSold(false);

		UserEntity user = new UserEntity();
		user.setUserId(2L);
		product.setUser(user);

		ShoppingCartEntity shoppingCart = new ShoppingCartEntity();
		shoppingCart.setId(1L);

		List<ShoppingCartEntity> carts = new ArrayList<>();
		carts.add(shoppingCart);
		
		List<ProductCartEntity> proCart = new ArrayList<>();
		ProductCartEntity p = null;
		proCart.add(p);
		product.setProductCarts(proCart);

		FeaturedProductEntity featuredProduct = new FeaturedProductEntity();
		featuredProduct.setFeaturedId(1L);
		List<FeaturedProductEntity> featuredProducts = new ArrayList<>();
		featuredProducts.add(featuredProduct);
		product.setFeaturedProducts(featuredProducts);
		List<MultipartFile> images = new ArrayList<>();
		product.setImages(images);
		List<String> imagePaths = new ArrayList<>();
        product.setImagePaths(imagePaths);

		Assertions.assertEquals(1L, product.getProductId());
		Assertions.assertEquals("product", product.getName());
		Assertions.assertEquals("detalle", product.getDetail());
		Assertions.assertNotNull(product.getProductDate());
		Assertions.assertNotNull(product.getUpdateDate());
		Assertions.assertEquals(50.0, product.getPrice());
		Assertions.assertEquals(10, product.getStock());
		Assertions.assertEquals(CategoryEnum.ELECTRODOMESTICOS, product.getCategory());
		Assertions.assertEquals(100, product.getNumOfViews());
		Assertions.assertFalse(product.isSold());
		Assertions.assertEquals(user, product.getUser());
		Assertions.assertEquals(proCart, product.getProductCarts());
		Assertions.assertEquals(featuredProducts, product.getFeaturedProducts());
		Assertions.assertEquals(images, product.getImages());
		Assertions.assertEquals(imagePaths, product.getImagePaths());

		ProductEntity sameProduct = new ProductEntity();
		sameProduct.setProductId(1L);
		Assertions.assertEquals(product.hashCode(), sameProduct.hashCode());
		Assertions.assertEquals(product, sameProduct);

	}
	 @Test
	    void equals_SameProductId_ReturnsTrue() {
	        Long productId = 1L;
	        ProductEntity product1 = new ProductEntity();
	        product1.setProductId(productId);
	        ProductEntity product2 = new ProductEntity();
	        product2.setProductId(productId);
	        assertTrue(product1.equals(product2));
	    }

	    @Test
	    void equals_DifferentProductId_ReturnsFalse() {
	        ProductEntity product1 = new ProductEntity();
	        product1.setProductId(1L);
	        ProductEntity product2 = new ProductEntity();
	        product2.setProductId(2L);
	        assertFalse(product1.equals(product2));
	    }

	    @Test
	    void equals_NullObject_ReturnsFalse() {
	        ProductEntity product = new ProductEntity();
	        assertFalse(product.equals(null));
	    }

	    @Test
	    void equals_DifferentClassObject_ReturnsFalse() {
	        ProductEntity product = new ProductEntity();
	        assertFalse(product.equals(new Object()));
	    }

	    @Test
	    void compradoPor_UserHasPurchased_ReturnsTrue() {
	        String username = "user@example.com";
	        ShoppingCartEntity cart = mock(ShoppingCartEntity.class);
	        UserEntity user = mock(UserEntity.class);
	        when(user.getEmail()).thenReturn(username);
	        ProductCartEntity productCart = mock(ProductCartEntity.class);
	        when(productCart.getCart()).thenReturn(cart);
	        when(cart.getUser()).thenReturn(user);
	        List<ProductCartEntity> productCarts = new ArrayList<>();
	        productCarts.add(productCart);

	        ProductEntity product = new ProductEntity();
	        product.setProductCarts(productCarts);

	        assertTrue(product.compradoPor(username));
	    }

	    @Test
	    void compradoPor_UserHasNotPurchased_ReturnsFalse() {
	        String username = "user@example.com";
	        ShoppingCartEntity cart = mock(ShoppingCartEntity.class);
	        UserEntity user = mock(UserEntity.class);
	        when(user.getEmail()).thenReturn("other@example.com"); // Different username
	        ProductCartEntity productCart = mock(ProductCartEntity.class);
	        when(productCart.getCart()).thenReturn(cart);
	        when(cart.getUser()).thenReturn(user);
	        List<ProductCartEntity> productCarts = new ArrayList<>();
	        productCarts.add(productCart);

	        ProductEntity product = new ProductEntity();
	        product.setProductCarts(productCarts);

	        assertFalse(product.compradoPor(username));
	    }
	
}
