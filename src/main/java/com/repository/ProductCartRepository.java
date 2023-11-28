package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.ProductCartEntity;

@Repository
public interface ProductCartRepository extends JpaRepository<ProductCartEntity, Long>{

}
