package com.tfg.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tfg.entity.ProductEntity;
import com.tfg.entity.enums.CategoryEnum;


public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

	ProductEntity findByProductId(Long productId);
	
	ProductEntity findByName(String name);
	
	@Query("SELECT p FROM ProductEntity p " +
	           "WHERE MONTH(p.productDate) = MONTH(CURRENT_DATE) " +
	           "GROUP BY p " +
	           "ORDER BY SUM(p.numOfViews) DESC")
	List<ProductEntity> findMostPopularProductsThisMonthLimited(Pageable pageable);

	
	@Query("SELECT p FROM ProductEntity p LEFT JOIN p.reviews r GROUP BY p.productId ORDER BY AVG(r.rating) DESC")
    List<ProductEntity> findAllByOrderByAverageRatingDesc();

	List<ProductEntity> findByUserUserId(Long userId);

	List<ProductEntity> findAllByUserUserIdIsNot(Long userId);

	

	List<ProductEntity> findByNameContainingIgnoreCase(String searchTerm);

	List<ProductEntity> findByUserUserIdAndSoldTrue(Long userId);

	List<ProductEntity> findByTagsIn(List<String> tags);

	List<ProductEntity> findByCategory(CategoryEnum category);
	
	

}
