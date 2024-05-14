package com.tfg.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.entity.ProductCartEntity;
import com.tfg.entity.ProductEntity;
import com.tfg.entity.ReviewEntity;
import com.tfg.entity.ShoppingCartEntity;
import com.tfg.entity.UserEntity;
import com.tfg.entity.enums.CategoryEnum;
import com.tfg.entity.enums.RoleEnum;
import com.tfg.repository.ProductCartRepository;
import com.tfg.repository.ProductRepository;
import com.tfg.repository.ReviewRepository;
import com.tfg.repository.ShoppingCartRepository;
import com.tfg.repository.UserRepository;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
	
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
	void findProductCartsById() {
		saveEntity();
		
		List<ProductCartEntity> result = prodCRepo.findProductCartsByCartId(1L);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.get(0).getProduct().getProductId(), 1L);
		Assertions.assertNotNull(result.get(0).getCart().getProductCartEntities());
	}
	
	
	
	  
	@Test
	void getById() {
		saveEntity();
		Optional<ShoppingCartEntity> result = sRepo.findById(1L);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result.get().getUser());
		Assertions.assertNotNull(result.get().getProductCartEntities());
		Assertions.assertNotNull(result.get().getTotalOrderPrice());
		
		
	}
	
	@Test
	void getByUserId() {
		saveEntity();
		List<ShoppingCartEntity> result = sRepo.getByUserId(1L);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result.get(0).getUser());
		Assertions.assertNotNull(result.get(0).getProductCartEntities());
		Assertions.assertNotNull(result.get(0).getTotalOrderPrice());
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
