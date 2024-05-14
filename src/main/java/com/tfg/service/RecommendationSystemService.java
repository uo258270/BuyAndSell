package com.tfg.service;

import java.util.List;

import com.tfg.entity.ProductEntity;
import com.tfg.exception.NullDataException;

public interface RecommendationSystemService {

	List<ProductEntity> getMostPopularProducts() throws Exception;

	List<ProductEntity> getTopRatedProducts() throws Exception;

	List<ProductEntity> getRecommendedProducts(Long userId) throws NullDataException;

	List<ProductEntity> findSimilarProductsByTags(Long productId);

	void calcularSimiProductos();

	void executeDailySimilarityCalculation();

	void executeDailySimilarityCalculationReviews();
	
	

}
