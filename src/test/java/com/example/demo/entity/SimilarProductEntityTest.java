package com.example.demo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import com.entity.ProductEntity;
import com.entity.SimilarProductEntity;

public class SimilarProductEntityTest {

	@Test
	public void testSimilarProductEntity() {

		ProductEntity prod1 = mock(ProductEntity.class);
		ProductEntity prod2 = mock(ProductEntity.class);

		SimilarProductEntity similarProduct = new SimilarProductEntity();
		similarProduct.setId(1L);
		similarProduct.setSimilarity(0.8);
		similarProduct.setProd1(prod1);
		similarProduct.setProd2(prod2);
		assertEquals(1L, similarProduct.getId());
		assertEquals(0.8, similarProduct.getSimilarity());
		assertEquals(prod1, similarProduct.getProd1());
		assertEquals(prod2, similarProduct.getProd2());
	}
}
