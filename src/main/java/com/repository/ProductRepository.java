package com.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.entity.ProductEntity;
import com.entity.ReviewEntity;


public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

	ProductEntity findByProductId(Long productId);
	
	ProductEntity findByName(String name);
	
	@Transactional
	Integer deleteByProductId(Long productId);
	
	@Query("SELECT p FROM ProductEntity p " +
		       "WHERE MONTH(p.productDate) = MONTH(CURRENT_DATE) " +
		       "ORDER BY p.numOfViews DESC")
		List<ProductEntity> findMostPopularProductsThisMonthLimited(Pageable pageable);

	
	@Query("SELECT p FROM ProductEntity p LEFT JOIN p.reviews r GROUP BY p.productId ORDER BY AVG(r.rating) DESC")
    List<ProductEntity> findAllByOrderByAverageRatingDesc();

	List<ProductEntity> findByUserUserId(Long userId);

	List<ProductEntity> findAllByUserUserIdIsNot(Long userId);

	

	List<ProductEntity> findByNameContainingIgnoreCase(String searchTerm);

	List<ProductEntity> findByUserUserIdAndSoldTrue(Long userId);
	
	

}
