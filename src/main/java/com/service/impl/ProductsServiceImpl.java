package com.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.entity.ProductEntity;
import com.repository.ProductRepository;

import com.service.ProductsService;

import jakarta.transaction.Transactional;

@Service
public class ProductsServiceImpl implements ProductsService{
	
	@Autowired
	private ProductRepository productRepository;
	

	public ProductsServiceImpl(ProductRepository productRepository) {
		
		this.productRepository = productRepository;
	}


	@Override
	public Page<ProductEntity> getProducts(Pageable pageable) {
		Page<ProductEntity> product = productRepository.findAll(pageable);
		return product;
	}
	

	@Override
	public ProductEntity findById(Long productId) throws Exception {
		if(productId!=null) {
			ProductEntity response = productRepository.findByProductId(productId);
			if(response!=null) {
				return response;
			}else {
				throw new Exception("Query does not found results");
			}
		}else {
			throw new Exception("productId can not be null");
		}
		
	}

	@Override
	public ProductEntity findByName(String name) throws Exception {
		if(name!=null) {
			ProductEntity response = productRepository.findByName(name);
			if(response!=null) {
				return response;
			}else {
				throw new Exception("Query does not found results");
			}
		}else {
			throw new Exception("name can not be null");
		}
	}

	@Override
	public void addProduct(ProductEntity product) throws Exception {
		validateProduct(product);
		productRepository.save(product);
		
	}

	private void validateProduct(ProductEntity product) throws Exception {
		if(product !=null) {
			if(product.getProductId()!=null) {
				ProductEntity response = productRepository.findByProductId(product.getProductId());
				if(response!=null) {
					throw new Exception("This product already exists in the data base");
				}
				throw new Exception("productId can not be null");
			}
			throw new Exception("product can not be null");
		}
		
	}

	@Override
	public void deleteProduct(Long productId) throws Exception {
		if(productId!=null) {
			 Integer response = productRepository.deleteProduct(productId);
			 if(response == null) {
					throw new Exception("this product can not be deleted");
			 }
		}
		else {
			throw new Exception("productId can not be null");
		}
		
	}


	@Transactional
	@Override
	public boolean updateProduct(ProductEntity product) {
		// TODO Auto-generated method stub
		return false;
	}

}
