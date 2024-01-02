package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.entity.FeaturedProductEntity;
import com.entity.ProductEntity;

public interface FeaturedRepository extends JpaRepository<FeaturedProductEntity, Long>{
	
	@Query("Select f from FeaturedProductEntity f where f.featuredId = :id")
	FeaturedProductEntity getByProductId(Long id);
	
	@Query("Select f from FeaturedProductEntity f  where f.user.userId= :userId")
	List<FeaturedProductEntity> getByUserId(Long userId);

	@Query("Select f from FeaturedProductEntity f where f.product.id=:productId")
	FeaturedProductEntity isFav(Long productId);
	
	

}
