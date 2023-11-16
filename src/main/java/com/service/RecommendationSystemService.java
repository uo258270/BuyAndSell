package com.service;

import java.util.List;

import com.entity.ProductEntity;

public interface RecommendationSystemService {

	List<ProductEntity> getMostPopularProducts() throws Exception;

	List<ProductEntity> getTopRatedProducts() throws Exception;

	List<ProductEntity> getProductsBySimilarReviewUsers(Long userId);

	List<ProductEntity> getProductsBySimilarUserCarts(Long userId);

}
