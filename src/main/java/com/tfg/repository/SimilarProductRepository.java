package com.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.entity.SimilarProductEntity;

public interface SimilarProductRepository extends JpaRepository<SimilarProductEntity, Long> {

}
