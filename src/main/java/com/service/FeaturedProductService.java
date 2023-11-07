package com.service;

import java.util.List;

import com.entity.FeaturedProductEntity;
import com.entity.ProductEntity;
import com.entity.UserEntity;

public interface FeaturedProductService {
	
	//listar isFeatured
	Boolean isFeatured(ProductEntity product);
	//getForUser
	List<FeaturedProductEntity> findByUser(UserEntity user) throws Exception;
	//add
	//añadir add
	void addFeatured(ProductEntity product) throws Exception;

	//delete
	void deleteFeaturedProduct(FeaturedProductEntity featured) throws Exception;

}