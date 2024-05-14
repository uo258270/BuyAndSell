package com.tfg.entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.entity.ProductEntity;
import com.tfg.entity.ReviewEntity;
import com.tfg.entity.UserEntity;


@ExtendWith(MockitoExtension.class)
public class ReviewEntityTest {
	
	@Test
    void reviewFields() {
        UserEntity user = new UserEntity();
        user.setUserId(1L);

        ProductEntity product = new ProductEntity();
        product.setProductId(1L);

        ReviewEntity review1 = new ReviewEntity();
        review1.setRatingId(1L);
        review1.setRating(4);
        review1.setComment("Good product");
        review1.setUserEntity(user);
        review1.setProduct(product);

       
        Assertions.assertEquals(1L, review1.getRatingId());
        Assertions.assertEquals(4, review1.getRating());
        Assertions.assertEquals("Good product", review1.getComment());
        Assertions.assertEquals(user, review1.getUserEntity());
        Assertions.assertEquals(product, review1.getProduct());

       
        ReviewEntity review2 = new ReviewEntity();
        review2.setRatingId(1L);
        review2.setRating(4);
        review2.setComment("Good product");
        review2.setUserEntity(user);
        review2.setProduct(product);

        Assertions.assertEquals(review1, review2);
        Assertions.assertEquals(review1.hashCode(), review2.hashCode());
    }

	@Test
    void validateReview_exception() {
        ReviewEntity review = new ReviewEntity();
        review.setRating(6); 

        Assertions.assertThrows(IllegalArgumentException.class, () -> {  validateReview(review);  });
    }

    private void validateReview(ReviewEntity review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }
    }

}
