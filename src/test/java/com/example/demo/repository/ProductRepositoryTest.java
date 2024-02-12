package com.example.demo.repository;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class ProductRepositoryTest {
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
	
	@Test
	void findByProductId() {
		saveEntity();
		ProductEntity result = prodRepo.findByProductId(1L);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result);
		
	}
	
	@Test
	void findByName() {
		saveEntity();
		ProductEntity result = prodRepo.findByName("1");
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result.getDetail());
		Assertions.assertNotNull(result.getPrice());
		
	}
	@Test
	void findMostPopularProductsThisMonthLimited() {
		saveEntity();
		Pageable pageable = PageRequest.of(0, 5);
		List<ProductEntity> result = prodRepo.findMostPopularProductsThisMonthLimited(pageable);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result.get(0));
		Assertions.assertNotNull(result.get(0).getDetail());
		Assertions.assertNotNull(result.get(0).getPrice());
		
	}
	
	@Test
	void findAllByOrderByAverageRatingDesc() {
		saveEntity();
		List<ProductEntity> result = prodRepo.findAllByOrderByAverageRatingDesc();
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result.get(0).getDetail());
		Assertions.assertNotNull(result.get(0).getPrice());
		
	}
	@Test
	void findByUserUserId() {
		saveEntity();
		List<ProductEntity> result = prodRepo.findByUserUserId(1L);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result);
		
	}
	
	@Test
	void findAllByUserUserIdIsNot() {
		saveEntity();
		List<ProductEntity> result = prodRepo.findAllByUserUserIdIsNot(1L);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result.get(0).getUser());
		Assertions.assertNotNull(result.get(0).getDetail());
		Assertions.assertNotNull(result.get(0).getPrice());
		
	}
	
	@Test
	void findByNameContainingIgnoreCase() {
		saveEntity();
		List<ProductEntity> result = prodRepo.findByNameContainingIgnoreCase("1");
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result);
		
	}
	
	@Test
	void findByUserUserIdAndSoldTrue() {
		saveEntity();
		List<ProductEntity> result = prodRepo.findByUserUserIdAndSoldTrue(1L);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result.get(0).getUser());
		Assertions.assertNotNull(result.get(0).getDetail());
		Assertions.assertNotNull(result.get(0).getPrice());
		
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

		ReviewEntity rev = new ReviewEntity();
		rev.setRatingId(1L);
		rev.setComment("1");
		rev.setRating(1);

		revRepo.save(rev);

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
