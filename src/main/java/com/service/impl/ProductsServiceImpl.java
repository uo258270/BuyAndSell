package com.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.exception.NotFoundException;
import com.exception.UpdateProductException;
import com.repository.FeaturedRepository;
import com.repository.ProductRepository;
import com.repository.ReviewRepository;
import com.service.ProductsService;

import jakarta.transaction.Transactional;

@Service
public class ProductsServiceImpl implements ProductsService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private FeaturedRepository featuredProductRepository;

	public ProductsServiceImpl(ProductRepository productRepository) {

		this.productRepository = productRepository;
	}

	@Override
	public List<ProductEntity> getProducts(Long userId) {
		List<ProductEntity> product = productRepository.findByUserUserId(userId);
		if (product != null) {
			return product;
		} else {
			throw new NotFoundException("This user does not have any published products");
		}
	}

	@Override
	public List<ProductEntity> getProductsExceptOwn(Long userId) {
		List<ProductEntity> product = productRepository.findAllByUserUserIdIsNot(userId);
		if (product != null) {
			return product;
		} else {
			throw new NotFoundException("There are no products published yet");
		}
	}

	@Override
	public ProductEntity findById(Long productId) throws Exception {
		if (productId != null) {
			ProductEntity product = productRepository.findByProductId(productId);
			if (product != null) {
				product.getUser().getUserId();
				product.getReviews().size();
				return product;
			} else {
				throw new Exception("Product not found");
			}
		} else {
			throw new Exception("ProductId cannot be null");
		}

	}

	@Override
	public ProductEntity findByName(String name) throws Exception {
		if (name != null) {
			ProductEntity response = productRepository.findByName(name);
			if (response != null) {
				return response;
			} else {
				throw new Exception("Query does not found results");
			}
		} else {
			throw new Exception("name can not be null");
		}
	}

	@Override
	public void addProduct(ProductEntity product) throws Exception {
		productRepository.save(product);

	}

	@Override
	public void deleteProduct(Long productId) throws Exception {
	    if (productId != null) {
	        try {
				featuredProductRepository.deleteById(productId);
				reviewRepository.deleteById(productId);
				productRepository.deleteById(productId);
			} catch (Exception e) {
				throw new Exception("product cannot be deleted");
			}

	     
	    } else {
	        throw new Exception("ProductId cannot be null");
	    }
	}


	@Override
	public double calculateAverageRating(Long productId) {
		List<ReviewEntity> reviews = reviewRepository.findByProductProductId(productId);

		if (reviews == null || reviews.isEmpty()) {
			return 0.0;
		}

		int totalRatings = 0;
		for (ReviewEntity review : reviews) {
			totalRatings += review.getRating();
		}

		return (double) totalRatings / reviews.size();
	}

	@Transactional
	@Override
	public boolean updateProduct(ProductEntity product, ProductEntity editedProduct) throws UpdateProductException {
		if (product.getProductId() == null) {
			throw new IllegalArgumentException("El identificador del producto no puede ser nulo para la actualizacion");
		}
		product.setName(editedProduct.getName());
		product.setDetail(editedProduct.getDetail());
		product.setPrice(editedProduct.getPrice());
		product.setStock(editedProduct.getStock());
		product.setCategory(editedProduct.getCategory());
		product.setUpdateDate(new Date());
		product.setImages(editedProduct.getImages());
		if (productRepository.save(product) != null) {
			return true;
		} else {
			throw new UpdateProductException("Error al actualizar el producto");
		}

	}

	@Override
	public List<ProductEntity> searchProducts(String searchTerm) {
		List<ProductEntity> prod = productRepository.findByNameContainingIgnoreCase(searchTerm);
		if (prod != null) {
			return prod;
		} else {
			throw new RuntimeException("No results found");
		}

	}

	@Override
	public List<ProductEntity> getSoldProducts(Long userId) {
		return productRepository.findByUserUserIdAndSoldTrue(userId);
	}

}
