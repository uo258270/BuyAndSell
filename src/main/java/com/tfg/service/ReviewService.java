package com.tfg.service;

import java.util.List;

import com.tfg.entity.ReviewEntity;
import com.tfg.exception.UnauthorizedException;

public interface ReviewService {

	List<ReviewEntity> getReviewsByProductId(Long productId);

	ReviewEntity addReview(ReviewEntity review) throws UnauthorizedException;

	void deleteReview(Long id);

	List<ReviewEntity> getAllReviews();

	Long findById(Long value);

}
