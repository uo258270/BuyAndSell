package com.tfg.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tfg.entity.ShoppingCartEntity;

import jakarta.transaction.Transactional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCartEntity, Long> {

	@Query("Select f from ShoppingCartEntity f where f.user.userId= :userId")
	List<ShoppingCartEntity> getByUserId(Long userId);

	@Query("Select f from ShoppingCartEntity f where f.id= :id")
	ShoppingCartEntity getById(Long id);

	@Transactional
	@Query("SELECT DISTINCT s FROM ShoppingCartEntity s LEFT JOIN FETCH s.productCartEntities WHERE s.user.id = :userId")
	List<ShoppingCartEntity> getShoppingCartsByUserId(@Param("userId") Long userId);

	@Query("SELECT COUNT(pce) FROM ShoppingCartEntity s JOIN s.productCartEntities pce " +
		       "WHERE s.user.userId = :userId AND pce.product.productId = :productId")
		Long countByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);


	
}
