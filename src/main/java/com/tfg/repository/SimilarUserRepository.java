package com.tfg.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tfg.entity.SimilarUserEntity;

public interface SimilarUserRepository extends JpaRepository<SimilarUserEntity, Long> {
	
    @Query("SELECT s FROM SimilarUserEntity s WHERE s.user1.userId = :userId OR s.user2.userId = :userId")
    List<SimilarUserEntity> getSimilarUsersByUserId(Long userId);
	 
	List<SimilarUserEntity> findAll();

}
