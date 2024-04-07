package com.service;

import java.util.List;

import com.entity.ProductEntity;
import com.exception.NullDataException;

public interface RecommendationSystemService {

	List<ProductEntity> getMostPopularProducts() throws Exception;

	List<ProductEntity> getTopRatedProducts() throws Exception;

	List<ProductEntity> getRecommendedProducts(Long userId) throws NullDataException;

	List<ProductEntity> findSimilarProductsByTags(Long productId);

	void calcularSimiProductos();

	void executeDailySimilarityCalculation();

	void executeDailySimilarityCalculationReviews();
	
	

}
