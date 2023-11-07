package com.repository;

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
	
	

}
