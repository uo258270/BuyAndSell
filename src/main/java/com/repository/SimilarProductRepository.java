package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.SimilarProductEntity;

public interface SimilarProductRepository extends JpaRepository<SimilarProductEntity, Long> {

}
