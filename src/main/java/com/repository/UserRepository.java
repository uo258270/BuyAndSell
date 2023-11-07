package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.entity.UserEntity;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{

	UserEntity findByUserId(Long userId);
	
	UserEntity findByEmail(String email);
	
	UserEntity findByUsername(String username);
	
	@Transactional
	@Query("Delete from UserEntity where userId = :id")
	Integer deleteUser(Long id);
	
	
	
}
