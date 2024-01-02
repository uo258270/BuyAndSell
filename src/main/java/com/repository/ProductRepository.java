package com.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.entity.ProductEntity;
import com.entity.ReviewEntity;


@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

	ProductEntity findByProductId(Long productId);
	
	ProductEntity findByName(String name);
	
	@Transactional
	@Query("Delete from ProductEntity where productId = :id")
	Integer deleteProduct(Long id);
	
	@Query("SELECT p FROM ProductEntity p " +
		       "WHERE MONTH(p.productDate) = MONTH(CURRENT_DATE) " +
		       "ORDER BY p.numOfViews DESC")
		List<ProductEntity> findMostPopularProductsThisMonthLimited(Pageable pageable);

	
	@Query("SELECT p FROM ProductEntity p LEFT JOIN p.reviews r GROUP BY p.productId ORDER BY AVG(r.rating) DESC")
    List<ProductEntity> findAllByOrderByAverageRatingDesc();

	List<ProductEntity> findByUserUserId(Long userId);

	List<ProductEntity> findAllByUserUserIdIsNot(Long userId);

	List<ReviewEntity> findReviewsByProductId(Long productId);

	List<ProductEntity> findByNameContainingIgnoreCase(String searchTerm);

	List<ProductEntity> findByUserIdAndSoldTrue(Long userId);

}
