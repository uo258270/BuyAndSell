package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.entity.ShoppingCartEntity;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCartEntity, Long>{

	
	//preguntar
	@Query("Select f from ShoppingCartEntity f where f.user.userId= :userId")
	List<ShoppingCartEntity> getByUserId(Long userId);
	
	
	@Query("Select f from ShoppingCartEntity f where f.id= :id")
	ShoppingCartEntity getById(Long id);

}
