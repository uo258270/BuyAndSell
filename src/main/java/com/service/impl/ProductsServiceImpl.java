package com.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.FeaturedProductEntity;
import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.exception.NotFoundException;
import com.exception.NullDataException;
import com.exception.UpdateProductException;
import com.repository.FeaturedRepository;
import com.repository.ProductCartRepository;
import com.repository.ProductRepository;
import com.repository.ReviewRepository;
import com.service.ProductsService;

import jakarta.transaction.Transactional;

@Service
public class ProductsServiceImpl implements ProductsService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private FeaturedRepository favRepo;

	@Autowired
	private ProductCartRepository productCartRepository;
	@Autowired
	private ReviewRepository reviewRepository;

	public ProductsServiceImpl(ProductRepository productRepository, ProductCartRepository productCartRepository,
			ReviewRepository reviewRepository, FeaturedRepository favRepo) {
		super();
		this.productRepository = productRepository;
		this.productCartRepository = productCartRepository;
		this.reviewRepository = reviewRepository;
		this.favRepo = favRepo;
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
	public ProductEntity findById(Long productId) throws NotFoundException {

		ProductEntity product = productRepository.findByProductId(productId);
		if (product != null) {
			product.getUser().getUserId();
			product.getReviews().size();
			return product;
		} else {
			throw new NotFoundException("Product not found");
		}

	}

	@Override
	public ProductEntity findByName(String name) throws Exception {

		ProductEntity response = productRepository.findByName(name);
		if (response != null) {
			return response;
		} else {
			throw new Exception("Query does not found results");
		}

	}

	@Override
	public void addProduct(ProductEntity product) {
		productRepository.save(product);

	}

	@Override
	public void deleteProduct(Long productId) throws NotFoundException, IllegalArgumentException {
		if (productId != null) {
			try {
				productRepository.deleteById(productId);
			} catch (RuntimeException ex) {
				throw new RuntimeException("Failed to delete product", ex);
			}
		} else {
			throw new IllegalArgumentException("ProductId cannot be null");
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
		ProductEntity updatedProduct = productRepository.save(product);
		if (updatedProduct != null) {
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

	@Transactional
	@Override
	public List<ProductEntity> getPurchasedProducts(Long userId) throws NullDataException {

		if (userId == null) {
			throw new NullDataException("el campo de entrada userId no puede ser null");
		}
		List<ProductEntity> purchasedProducts = new ArrayList<>();
		purchasedProducts = productCartRepository.getPurchasedProductsByUserId(userId);
		return purchasedProducts;
	}

	@Override
	public boolean checkIfProductIsFavorite(Long productId) {

		ProductEntity product = productRepository.findByProductId(productId);
		if (product != null) {
			List<FeaturedProductEntity> featuredProducts = favRepo.getFeaturedProductsByProductId(productId);
			for (FeaturedProductEntity featuredProduct : featuredProducts) {
				if (featuredProduct.getProduct().getProductId().equals(productId)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public ProductEntity increaseNumOfViews(ProductEntity product) {
		try {
			product.setNumOfViews(product.getNumOfViews()+1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return product;
	}

}
