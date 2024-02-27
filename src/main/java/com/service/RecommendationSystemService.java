package com.service;

import java.util.List;

import com.entity.ProductEntity;

public interface RecommendationSystemService {

	List<ProductEntity> getMostPopularProducts() throws Exception;

	List<ProductEntity> getTopRatedProducts() throws Exception;

	List<ProductEntity> getRecommendedProducts(Long userId);

}
