package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.entity.FeaturedProductEntity;

public interface FeaturedRepository extends JpaRepository<FeaturedProductEntity, Long>{
	
	
	FeaturedProductEntity findByFeaturedId(Long featuredId);
	
	@Query("Select f from FeaturedProductEntity f  where f.user.userId= :userId")
	List<FeaturedProductEntity> getByUserId(Long userId);

	@Query("SELECT f FROM FeaturedProductEntity f WHERE f.product.productId = :productId AND f.user.userId = :userId")
	FeaturedProductEntity isFav(Long productId, Long userId);

	@Query("SELECT fp FROM FeaturedProductEntity fp WHERE fp.product.productId = :productId")
	List<FeaturedProductEntity> getFeaturedProductsByProductId( Long productId);
	

}
