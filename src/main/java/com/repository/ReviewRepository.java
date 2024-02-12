package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.entity.ProductEntity;
import com.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long>{

	List<ReviewEntity> findByProductProductId(Long productId);
	
	@Query("SELECT r.product FROM ReviewEntity r WHERE r.ratingId = :reviewId")
	ProductEntity getProductByRatingId( Long reviewId);

}
