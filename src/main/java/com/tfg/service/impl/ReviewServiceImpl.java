package com.tfg.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.entity.ProductEntity;
import com.tfg.entity.ReviewEntity;
import com.tfg.exception.NotFoundException;
import com.tfg.exception.UnauthorizedException;
import com.tfg.repository.ProductRepository;
import com.tfg.repository.ReviewRepository;
import com.tfg.service.ReviewService;

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

	@Override
	public List<ReviewEntity> getAllReviews() {
		return reviewRepository.findAll();
	}

	@Override
	public Long findById(Long value) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
