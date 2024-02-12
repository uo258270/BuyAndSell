package com.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.exception.NotFoundException;
import com.exception.UnauthorizedException;
import com.repository.ProductRepository;
import com.repository.ReviewRepository;
import com.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService{
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	
	
	public ReviewServiceImpl(ReviewRepository reviewRepository, ProductRepository productRepository) {
		super();
		this.reviewRepository = reviewRepository;
		this.productRepository = productRepository;
	}

	@Override
	public List<ReviewEntity> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductProductId(productId);
    }

	@Override
    public ReviewEntity addReview(ReviewEntity review) throws UnauthorizedException {
		 reviewRepository.save(review);
		 ProductEntity p  = reviewRepository.getProductByRatingId(review.getRatingId());
		 p.getReviews().add(review);
		 productRepository.save(p);
		 return review;
		
       
    }

	@Override
	public void deleteReview(Long id) throws NotFoundException{
		
        if (reviewRepository.findById(id) != null) {
            reviewRepository.deleteById(id);
        } else {
            throw new NotFoundException("La reseña con el ID " + id + " no fue encontrada");
        }
		
	}
	
	

}
