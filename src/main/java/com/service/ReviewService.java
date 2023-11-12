package com.service;

import java.util.List;

import com.entity.ReviewEntity;
import com.exception.UnauthorizedException;

public interface ReviewService {

	List<ReviewEntity> getReviewsByProductId(Long productId);

	ReviewEntity addReview(ReviewEntity review) throws UnauthorizedException;

	void deleteReview(Long id);

}
