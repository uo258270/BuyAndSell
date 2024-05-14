package com.tfg.entity;

import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.entity.FeaturedProductEntity;
import com.tfg.entity.ProductEntity;
import com.tfg.entity.UserEntity;

@ExtendWith(MockitoExtension.class)
public class FeaturedProductEntityTest {

	@Test
	void featuredProductCreation_EqualsAndHashCodeValidation() {
		ProductEntity product1 = new ProductEntity();
		product1.setProductId(1L);

		UserEntity user1 = new UserEntity();
		user1.setUserId(1L);

		FeaturedProductEntity featuredProduct1 = new FeaturedProductEntity();
		featuredProduct1.setFeaturedId(1L);
		featuredProduct1.setDate(new Date());
		featuredProduct1.setProduct(product1);
		featuredProduct1.setUser(user1);
		FeaturedProductEntity featuredProduct2 = new FeaturedProductEntity();
		featuredProduct2.setFeaturedId(1L);
		featuredProduct2.setDate(new Date());
		featuredProduct2.setProduct(product1);
		featuredProduct2.setUser(user1);

		Assertions.assertEquals(featuredProduct1, featuredProduct2);
		Assertions.assertEquals(featuredProduct1.hashCode(), featuredProduct2.hashCode());
		Assertions.assertEquals(1L, featuredProduct1.getFeaturedId());
		Assertions.assertEquals(product1, featuredProduct1.getProduct());
		Assertions.assertEquals(user1, featuredProduct1.getUser());
		Assertions.assertNotNull(featuredProduct1.getDate());
	}

	@Test
	void featuredProductCreation_DifferentInstances_NotEqual() {
		ProductEntity product1 = new ProductEntity();
		product1.setProductId(1L);

		UserEntity user1 = new UserEntity();
		user1.setUserId(1L);

		UserEntity user2 = new UserEntity();
		user2.setUserId(2L);
		FeaturedProductEntity featuredProduct1 = new FeaturedProductEntity();
		featuredProduct1.setFeaturedId(1L);
		featuredProduct1.setDate(new Date());
		featuredProduct1.setProduct(product1);
		featuredProduct1.setUser(user1);

		FeaturedProductEntity featuredProduct2 = new FeaturedProductEntity();
		featuredProduct2.setFeaturedId(1L);
		featuredProduct2.setDate(new Date());
		featuredProduct2.setProduct(product1);
		featuredProduct2.setUser(user2);

		Assertions.assertNotEquals(featuredProduct1, featuredProduct2);
	}
}
