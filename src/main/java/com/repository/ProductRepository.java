package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.entity.ProductEntity;


@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

	ProductEntity findByProductId(Long productId);
	
	ProductEntity findByName(String name);
	
	@Transactional
	@Query("Delete from ProductEntity where productId = :id")
	Integer deleteProduct(Long id);
	
	@Query("SELECT p FROM ProductEntity p " +
	           "WHERE MONTH(p.productDate) = MONTH(CURRENT_DATE) " +
	           "ORDER BY p.numOfViews DESC " +
	           "FETCH FIRST 5 ROWS ONLY")
	List<ProductEntity> findMostPopularProductsThisMonthLimited();
	
	@Query("SELECT p FROM ProductEntity p LEFT JOIN p.ratings r GROUP BY p.productId ORDER BY AVG(r.rating) DESC")
    List<ProductEntity> findAllByOrderByAverageRatingDesc();

}
