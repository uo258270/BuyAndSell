package com.tfg.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.entity.ShoppingCartEntity;
import com.tfg.entity.UserEntity;
import com.tfg.entity.enums.RoleEnum;


public interface UserRepository extends JpaRepository<UserEntity, Long>{

	UserEntity findByUserId(Long userId);
	
	UserEntity findByEmail(String email);
	
	UserEntity findByUsername(String username);
	
	@Transactional
	@Query("Delete from UserEntity where userId = :id")
	int deleteUser(Long id);

	List<UserEntity> findByRole(RoleEnum role);
	
	@Transactional
	@Query("SELECT DISTINCT s FROM ShoppingCartEntity s LEFT JOIN FETCH s.productCartEntities WHERE s.user.id = :userId")
	List<ShoppingCartEntity> getShoppingCartsByUserId(@Param("userId") Long userId);

	

	
	
}
