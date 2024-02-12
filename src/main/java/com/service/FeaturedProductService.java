package com.service;

import java.util.List;

import com.entity.FeaturedProductEntity;
import com.entity.ProductEntity;
import com.entity.UserEntity;

public interface FeaturedProductService {
	
	//getForUser
	List<FeaturedProductEntity> findByUser(UserEntity user) throws Exception;
	//add
	//añadir add
	void addFeatured(ProductEntity product, UserEntity user) throws Exception;

	//delete
	void deleteFeaturedProduct(FeaturedProductEntity featured) throws Exception;
	FeaturedProductEntity getFeaturedById(Long id) throws Exception;

	Boolean isFavourited(ProductEntity prod, Long userId);

}
