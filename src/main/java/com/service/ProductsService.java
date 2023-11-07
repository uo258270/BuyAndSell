package com.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.entity.ProductEntity;


public interface ProductsService {
	//listar findById
	ProductEntity findById(Long productId) throws Exception;
	//filtrar findByName
	ProductEntity findByName(String name) throws Exception;
	//añadir add
	void addProduct(ProductEntity product) throws Exception;
	//borrar delete
	void deleteProduct(Long productId) throws Exception;
	
	//modificar producto update
	boolean updateProduct(ProductEntity product);
	
	//listar productos
	Page<ProductEntity> getProducts(Pageable pageable);
}
