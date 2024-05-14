package com.tfg.service;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.entity.ProductCartEntity;
import com.tfg.entity.ProductEntity;
import com.tfg.entity.ReviewEntity;
import com.tfg.entity.ShoppingCartEntity;
import com.tfg.entity.UserEntity;
import com.tfg.exception.NotFoundException;
import com.tfg.exception.UnauthorizedException;
import com.tfg.repository.ProductRepository;
import com.tfg.repository.ReviewRepository;
import com.tfg.service.impl.ReviewServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplTest {

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;
    
    @BeforeEach
	public void init() {
		MockitoAnnotations.openMocks(this);
		reviewService = new ReviewServiceImpl(reviewRepository, productRepository);
	}


    @Test
    void getReviewsByProductId_ValidProductId_ReturnsListOfReviews() {
        Long productId = 1L;
        List<ReviewEntity> mockedReviews = new ArrayList<>();
        Mockito.when(reviewRepository.findByProductProductId(any())).thenReturn(mockedReviews);

        List<ReviewEntity> result = reviewService.getReviewsByProductId(productId);

        Assertions.assertEquals(mockedReviews, result);
    }

    @Test
    void addReview_ValidReviewAndPurchasedProduct_SavesReview() throws UnauthorizedException {
        ReviewEntity review = new ReviewEntity();
        review.setRatingId(1L);
        review.setRating(4);
        review.setComment("1");
       
        UserEntity user = new UserEntity();
        ProductEntity product = new ProductEntity();
        product.setProductId(1L);
        review.setProduct(product);
        review.setUserEntity(user);
        List<ReviewEntity> list = new ArrayList<>();
        list.add(review);
		product.setReviews(list);

        List<ShoppingCartEntity> carts = new ArrayList<>();
        ShoppingCartEntity cart = new ShoppingCartEntity();
        ProductCartEntity cartProduct = new ProductCartEntity();
        cartProduct.setProduct(product);
        carts.add(cart);
        user.setCarts(carts);

        Mockito.when(reviewRepository.save(any())).thenReturn(review);
        Mockito.when(reviewRepository.getProductByRatingId(any())).thenReturn(product);
        Mockito.when(productRepository.save(any())).thenReturn(product);

        ReviewEntity result = reviewService.addReview(review);

        Assertions.assertEquals(review, result);
        Assertions.assertEquals(result.getRatingId(), 1L);
    }

   
    @Test
    void deleteReview_ValidId_DeletesReview() {
        Long id = 1L;
        Mockito.when(reviewRepository.findById(any())).thenReturn(Optional.of(new ReviewEntity()));

        reviewService.deleteReview(id);

       
    }

    @Test
    void deleteReview_InvalidId_ThrowsNotFoundException() {
        Long id = 1L;
        Mockito.when(reviewRepository.findById(any())).thenReturn(null);

        Assertions.assertThrows(NotFoundException.class, () -> reviewService.deleteReview(id));
      
    }

   
}
