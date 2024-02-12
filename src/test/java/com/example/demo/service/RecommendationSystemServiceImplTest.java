package com.example.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repository.ProductCartRepository;
import com.repository.ProductRepository;
import com.repository.ShoppingCartRepository;
import com.repository.UserRepository;
import com.service.impl.RecommendationSystemServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.entity.ProductCartEntity;
import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.entity.enums.RoleEnum;
import com.exception.NullDataException;

@ExtendWith(MockitoExtension.class)
public class RecommendationSystemServiceImplTest {

	@InjectMocks
	private RecommendationSystemServiceImpl recService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ShoppingCartRepository shoRepo;

	@Mock
	private ProductCartRepository productCartRepository;

	@BeforeEach
	public void init() {
		MockitoAnnotations.openMocks(this);
		recService = new RecommendationSystemServiceImpl(productRepository, userRepository, shoRepo,
				productCartRepository);
	}

	@Test
	void getMostPopularProducts_OK() throws Exception {
		List<ProductEntity> mockedProducts = createMockedProductList();
		Mockito.when(productRepository.findMostPopularProductsThisMonthLimited(any())).thenReturn(mockedProducts);
		List<ProductEntity> result = recService.getMostPopularProducts();

		Assertions.assertEquals(mockedProducts, result);
	}

	@Test
	void getMostPopularProducts_NoProducts_Exception() {

		Mockito.when(productRepository.findMostPopularProductsThisMonthLimited(any())).thenReturn(null);

		NullDataException result = assertThrows(NullDataException.class, () -> recService.getMostPopularProducts());
		Assertions.assertEquals("There are no popular products", result.getMessage());

	}

	@Test
	void getProductsBySimilarReviewUsers_OKListOfProducts() {

		Long userId = 1L;
		UserEntity user = new UserEntity();
		user.setUserId(userId);

		UserEntity user1 = new UserEntity();
		user1.setUserId(2L);

		ReviewEntity review = new ReviewEntity();
		review.setRatingId(1L);
		review.setRating(4);
		review.setComment("1");

		ProductEntity product = new ProductEntity();
		product.setProductId(1L);
		review.setProduct(product);
		review.setUserEntity(user);
		List<ReviewEntity> list = new ArrayList<>();
		list.add(review);
		product.setReviews(list);

		user.setReviews(list);
		user1.setReviews(list);

		Mockito.when(userRepository.findByUserId(userId)).thenReturn(user);

		Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user, user1));

		List<ProductEntity> result = recService.getProductsBySimilarReviewUsers(userId);
		Assertions.assertEquals(result.get(0).getProductId(), product.getProductId());
	}

	@Test
	void getTopRatedProducts_OK() throws Exception {

		List<ProductEntity> mockedProducts = createMockedProductList();

		Mockito.when(productRepository.findAllByOrderByAverageRatingDesc()).thenReturn(mockedProducts);

		List<ProductEntity> result = recService.getTopRatedProducts();

		Assertions.assertEquals(mockedProducts, result);
	}

	@Test
	void getTopRatedProducts_KO() throws NullDataException {

		Mockito.when(productRepository.findAllByOrderByAverageRatingDesc()).thenReturn(null);

		NullDataException result = assertThrows(NullDataException.class, () -> recService.getTopRatedProducts());
		Assertions.assertEquals("there are the best rated products", result.getMessage());

	}

	@Test
	void getProductsBySimilarUserCarts_OK_returnsProds() {
		Long userId = 1L;
		UserEntity user = new UserEntity();
		user.setUserId(userId);

		List<ShoppingCartEntity> userPurchases = Arrays.asList(
				createShoppingCart(1L, Arrays.asList(createProductCart(4L))),
				createShoppingCart(2L, Arrays.asList(createProductCart(5L))));

		List<UserEntity> similarUsers = Arrays
				.asList(createUser(2L, Arrays.asList(createShoppingCart(3L, Arrays.asList(createProductCart(6L))))));

		List<ProductEntity> mockedProducts = createMockedProductList();

		mockedProducts.get(0).getUser().setUserId(99L);

		Mockito.when(userRepository.getShoppingCartsByUserId(any())).thenReturn(userPurchases);
		Mockito.when(userRepository.findAll()).thenReturn(similarUsers);
		List<Long> ids = Arrays.asList(1L, 2L, 3L, 4L);
		Mockito.when(productCartRepository.findProductIdsByShoppingCarts(any())).thenReturn(ids);
		Mockito.when(shoRepo.countByUserIdAndProductId(any(), any())).thenReturn(0L);
		Mockito.when(productRepository.findById(any())).thenReturn(Optional.of(mockedProducts.get(0)));

		List<ProductEntity> result = recService.getProductsBySimilarUserCarts(userId);

		Assertions.assertEquals(result.size(), 1);
	}

	@Test
	void getProductsBySimilarUserCarts_EmptyList() {
		Long userId = 1L;
		UserEntity user = new UserEntity();
		user.setUserId(userId);

		List<ShoppingCartEntity> userPurchases = Arrays.asList(
				createShoppingCart(1L, Arrays.asList(createProductCart(4L))),
				createShoppingCart(2L, Arrays.asList(createProductCart(5L))));
			List<UserEntity> similarUsers = Arrays
				.asList(createUser(2L, Arrays.asList(createShoppingCart(3L, Arrays.asList(createProductCart(6L))))));

		List<ProductEntity> mockedProducts = createMockedProductList();

		Mockito.when(shoRepo.countByUserIdAndProductId(any(), any())).thenReturn(1L);

		Mockito.when(userRepository.getShoppingCartsByUserId(any())).thenReturn(userPurchases);
		Mockito.when(userRepository.findAll()).thenReturn(similarUsers);
		List<Long> ids = Arrays.asList(1L, 2L, 3L, 4L);
		Mockito.when(productCartRepository.findProductIdsByShoppingCarts(any())).thenReturn(ids);
		Mockito.when(productRepository.findById(any())).thenReturn(Optional.of(mockedProducts.get(0)));

		List<ProductEntity> result = recService.getProductsBySimilarUserCarts(userId);

		Assertions.assertEquals(result.size(), 0);
	}

	@Test
	void calculatePearsonCorrelation_EmptyRatings() {
	 
	    UserEntity user1 = new UserEntity();
	    List<ReviewEntity> re = new ArrayList<>();
		user1.setReviews(re);
	    UserEntity user2 = new UserEntity();
	    user2.setReviews(re);

	    
	    double correlation = recService.calculatePearsonCorrelation(user1, user2);

	    Assertions.assertEquals(0.0, correlation);
	}


	@Test
	void calculatePearsonCorrelation_ReturnsCorrelation() {

		UserEntity user1 = createMockedUserWithReviews(1L, 4, 5, 3, 2);
		UserEntity user2 = createMockedUserWithReviews(2L, 3, 4, 5, 1);

		double correlation = recService.calculatePearsonCorrelation(user1, user2);

		double expectedCorrelation = 0.5;
		double roundedCorrelation = Math.round(correlation * 1000.0) / 1000.0;
		Assertions.assertEquals(expectedCorrelation, roundedCorrelation, 0.1);

	}

	private UserEntity createMockedUserWithReviews(Long userId, int... ratings) {
		UserEntity user = new UserEntity();
		user.setUserId(userId);

		List<ReviewEntity> reviews = new ArrayList<>();
		for (int i = 0; i < ratings.length; i++) {
			ReviewEntity review = new ReviewEntity();
			review.setRatingId((long) (i + 1));
			review.setRating(ratings[i]);

			
			ProductEntity product = new ProductEntity();
			product.setProductId((long) (i + 1));
			review.setProduct(product);

			reviews.add(review);
		}

		user.setReviews(reviews);

		return user;
	}

	@Test
	void getProductsBySimilarReviewUsers_MaxProds() {
		Long userId = 1L;
	    UserEntity user = createMockedUserWithReviews(userId, 4, 5, 3, 2, 1, 4, 5, 2, 3, 1, 5, 4, 3, 2, 1);

	   
	    UserEntity otherUser1 = createMockedUserWithReviews(2L, 3, 4, 5, 1, 5, 4, 3, 2, 1);
	    UserEntity otherUser2 = createMockedUserWithReviews(3L, 5, 4, 3, 2, 1, 4, 5, 2, 3);
	    UserEntity otherUser3 = createMockedUserWithReviews(4L, 1, 2, 3, 4, 5, 4, 3, 2, 1);
	    UserEntity otherUser4 = createMockedUserWithReviews(5L, 2, 3, 4, 5, 1, 4, 5, 2, 3);

	    Mockito.when(userRepository.findByUserId(userId)).thenReturn(user);
	    Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(otherUser1, otherUser2, otherUser3, otherUser4));

	    List<ProductEntity> result = recService.getProductsBySimilarReviewUsers(userId);

	  
	    Assertions.assertEquals(5, result.size());
	}
	private UserEntity createUser(Long userId, List<ShoppingCartEntity> carts) {
		UserEntity user = new UserEntity();
		user.setUserId(userId);
		user.setCarts(carts);
		return user;
	}

	private ProductEntity createMockedProduct() {
		Long productId = 1L;
		ProductEntity mockedProduct = new ProductEntity();
		mockedProduct.setProductId(productId);
		mockedProduct.setName("product");
		mockedProduct.setDetail("details");
		mockedProduct.setPrice(20.0);
		mockedProduct.setStock(10);

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
		mockedProduct.setUser(user);

		List<ReviewEntity> revs = new ArrayList<>();
		ReviewEntity rev = new ReviewEntity();
		rev.setRatingId(1L);
		rev.setComment("1");
		rev.setRating(1);
		revs.add(rev);
		mockedProduct.setReviews(revs);

		productRepository.save(mockedProduct);
		return mockedProduct;
	}

	private List<ProductEntity> createMockedProductList() {
		List<ProductEntity> mockedProducts = new ArrayList<>();

		for (int i = 1; i <= 5; i++) {
			Long productId = (long) i;

			ProductEntity mockedProduct = new ProductEntity();
			mockedProduct.setProductId(productId);
			mockedProduct.setName("product" + i);
			mockedProduct.setDetail("details" + i);
			mockedProduct.setPrice(20.0 + i);
			mockedProduct.setStock(10 + i);

			UserEntity user = new UserEntity();
			user.setUserId((long) i);
			user.setEmail(i + "@example.com");
			user.setLastName("Lastname" + i);
			user.setMoney(10.0 + i);
			user.setName("User" + i);
			user.setPassword("password" + i);
			user.setPasswordConfirm("password" + i);
			user.setRegisterDate(new Date());
			user.setRole(RoleEnum.ROLE_ADMIN);
			user.setUsername("username" + i);

			mockedProduct.setUser(user);

			List<ReviewEntity> revs = new ArrayList<>();
			ReviewEntity rev = new ReviewEntity();
			rev.setRatingId((long) i);
			rev.setComment("Comment" + i);
			rev.setRating(i);
			revs.add(rev);

			mockedProduct.setReviews(revs);
			mockedProducts.add(mockedProduct);
		}

		return mockedProducts;
	}

	private ShoppingCartEntity createShoppingCart(Long cartId, List<ProductCartEntity> productCarts) {
		ShoppingCartEntity cart = new ShoppingCartEntity();
		cart.setId(cartId);
		cart.setProductCartEntities(productCarts);
		return cart;
	}

	private ProductCartEntity createProductCart(Long productId) {
		ProductEntity product = createMockedProduct();
		product.setProductId(productId);

		ProductCartEntity productCart = new ProductCartEntity();
		productCart.setProduct(product);
		return productCart;
	}
}
