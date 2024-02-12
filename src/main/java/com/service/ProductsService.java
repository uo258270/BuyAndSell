package com.service;

import java.util.List;

import com.entity.ProductEntity;
import com.exception.NullDataException;
import com.exception.UpdateProductException;


public interface ProductsService {
	//listar findById
	ProductEntity findById(Long productId) throws Exception;
	//filtrar findByName
	ProductEntity findByName(String name) throws Exception;
	//añadir add
	void addProduct(ProductEntity product) throws Exception;
	//borrar delete
	void deleteProduct(Long productId) throws Exception;
	
	// producto update
	boolean updateProduct(ProductEntity product, ProductEntity editedProduct) throws UpdateProductException;
	
	//listar productos
	List<ProductEntity> getProducts(Long userId);
	
	List<ProductEntity> getProductsExceptOwn(Long userId);
	double calculateAverageRating(Long productId);
	
	List<ProductEntity> searchProducts(String searchTerm);
	
	List<ProductEntity> getSoldProducts(Long userId);
	List<ProductEntity> getPurchasedProducts(Long userId) throws NullDataException;
	boolean checkIfProductIsFavorite(Long productId);
	ProductEntity increaseNumOfViews(ProductEntity product);
}
