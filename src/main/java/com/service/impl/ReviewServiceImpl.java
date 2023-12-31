package com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.ProductCartEntity;
import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.exception.NotFoundException;
import com.exception.UnauthorizedException;
import com.repository.ReviewRepository;
import com.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService{
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Override
	public List<ReviewEntity> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductProductId(productId);
    }

	@Override
    public ReviewEntity addReview(ReviewEntity review) throws UnauthorizedException {
		
		if (hasUserPurchasedProduct(review.getUser(), review.getProduct())) {
            return reviewRepository.save(review);
        } else {
            throw new UnauthorizedException("El usuario no ha comprado el producto, no puede dejar una reseña.");
        }
       
    }

	@Override
	public void deleteReview(Long id) {
		
        if (reviewRepository.findById(id) != null) {
            reviewRepository.deleteById(id);
        } else {
            throw new NotFoundException("La reseña con el ID " + id + " no fue encontrada");
        }
		
	}
	
	public boolean hasUserPurchasedProduct(UserEntity user, ProductEntity product) {
	    List<ShoppingCartEntity> carts = user.getCarts();
	    
	    for (ShoppingCartEntity cart : carts) {
	        for (ProductCartEntity cartProduct : cart.getProductCartEntities()) {
	            if (cartProduct.getProduct().equals(product)) {
	                return true; 
	            }
	        }
	    }
	    return false;
	}

}
