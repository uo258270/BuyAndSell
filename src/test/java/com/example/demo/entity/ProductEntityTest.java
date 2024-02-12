package com.example.demo.entity;

import com.entity.FeaturedProductEntity;
import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.entity.enums.CategoryEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
		product.setCarts(carts);

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
		Assertions.assertEquals(carts, product.getCarts());
		Assertions.assertEquals(featuredProducts, product.getFeaturedProducts());
		Assertions.assertEquals(images, product.getImages());
		Assertions.assertEquals(imagePaths, product.getImagePaths());

		ProductEntity sameProduct = new ProductEntity();
		sameProduct.setProductId(1L);
		Assertions.assertEquals(product.hashCode(), sameProduct.hashCode());
		Assertions.assertEquals(product, sameProduct);

	}
}
