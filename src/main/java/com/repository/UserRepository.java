package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{

	UserEntity findByUserId(Long userId);
	
	UserEntity findByEmail(String email);
	
	UserEntity findByUsername(String username);
	
	@Transactional
	@Query("Delete from UserEntity where userId = :id")
	int deleteUser(Long id);

	List<UserEntity> findByRole(String string);
	
	@Query("SELECT user.carts FROM UserEntity user WHERE user.userId = :userId")
    List<ShoppingCartEntity> getShoppingCartsByUserId(Long userId);

	
	
}
