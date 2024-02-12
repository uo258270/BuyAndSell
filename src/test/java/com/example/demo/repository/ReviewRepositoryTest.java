package com.example.demo.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.entity.ProductCartEntity;
import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.entity.enums.CategoryEnum;
import com.entity.enums.RoleEnum;
import com.repository.ProductCartRepository;
import com.repository.ProductRepository;
import com.repository.ReviewRepository;
import com.repository.ShoppingCartRepository;
import com.repository.UserRepository;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReviewRepositoryTest {
	
	@Autowired
	ShoppingCartRepository sRepo;

	@Autowired
	ProductRepository prodRepo;

	@Autowired
	UserRepository userRepo;
	@Autowired
	ProductCartRepository prodCRepo;
	@Autowired
	ReviewRepository revRepo;
	
	ReviewEntity rev = new ReviewEntity();
	
	@Test
	void findByProductProductId() {
		saveEntity();
		List<ReviewEntity> result = revRepo.findByProductProductId(1L);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result.get(0).getRatingId());
		Assertions.assertNotNull(result.get(0).getComment());
		Assertions.assertNotNull(result.get(0).getRating());
		
	}
	
	
	@Test
	void findProductByRatingId() {
		saveEntity();
		ProductEntity result = revRepo.getProductByRatingId(rev.getRatingId());
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result.getName());
		Assertions.assertNotNull(result.getDetail());
		Assertions.assertNotNull(result.getPrice());
		
	}
	
	void saveEntity() {

		UserEntity user = new UserEntity();
		user.setUserId(1L);
		user.setEmail("1");
		user.setLastName("1");
		user.setMoney(10.0);
		user.setName("1");
		user.setPassword("1");
		user.setPasswordConfirm("1");
		user.setRegisterDate(new Date());
		user.setRole(RoleEnum.ROLE_ADMIN);
		user.setUsername("1");

		
		rev.setRatingId(1L);
		rev.setComment("1");
		rev.setRating(1);

		List<ReviewEntity> list= new ArrayList<>();
		user.setReviews(list);
		rev.setUserEntity(user);
		rev = revRepo.save(rev);

		ProductEntity prod = new ProductEntity();
		prod.setProductId(1L);
		prod.setCategory(CategoryEnum.ROPA);
		prod.setDetail("1");
		prod.setFeaturedProducts(null);
		List<String> im = null;
		prod.setImagePaths(im);
		List<MultipartFile> img = null;
		prod.setImages(img);
		prod.setName("1");
		prod.setNumOfViews(1);
		prod.setPrice(10.0);
		prod.setProductDate(new Date());
		prod.setSold(false);
		prod.setStock(2);
		
		prodRepo.save(prod);
		rev.setProduct(prod);
		revRepo.save(rev);

		ShoppingCartEntity sho = new ShoppingCartEntity();
		sho.setId(1L);
		sho.setUser(user);

		ProductCartEntity prodCart = new ProductCartEntity();
		prodCart.setId(1L);
		prodCart.setCart(sho);
		prodCart.setProduct(prod);
		prodCart.setQuantityInCart(1);

		userRepo.save(user);
		prodCRepo.save(prodCart);
		sRepo.save(sho);
	}

}
