package com.tfg.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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
import com.tfg.entity.SimilarProductEntity;
import com.tfg.entity.SimilarUserEntity;
import com.tfg.entity.UserEntity;
import com.tfg.entity.enums.RoleEnum;
import com.tfg.exception.NullDataException;
import com.tfg.repository.ProductCartRepository;
import com.tfg.repository.ProductRepository;
import com.tfg.repository.ShoppingCartRepository;
import com.tfg.repository.SimilarProductRepository;
import com.tfg.repository.SimilarUserRepository;
import com.tfg.repository.UserRepository;
import com.tfg.service.impl.RecommendationSystemServiceImpl;

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

	@Mock
	private SimilarUserRepository similarUserRepository;

	@Mock
	private SimilarProductRepository similarProductRepository;

	@BeforeEach
	public void init() {
		MockitoAnnotations.openMocks(this);
		recService = new RecommendationSystemServiceImpl(productRepository, userRepository, shoRepo,
				productCartRepository, similarProductRepository, similarUserRepository);
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
	public void testGetProductsBySimilarUserCarts_ReturnsRecommendedProducts() {
		Long userId = 1L;
		List<Long> userPurchaseIds = Arrays.asList(1L, 2L);
		List<Long> similarUserIds = Arrays.asList(2L, 3L);
		List<Long> similarUsers = new ArrayList<>(Arrays.asList(5L, 6L));
		ProductEntity prod = new ProductEntity();
		prod.setProductId(5L);
		List<ShoppingCartEntity> simiU = createShoppingCarts(similarUsers);
		when(userRepository.getShoppingCartsByUserId(userId)).thenReturn(createShoppingCarts(userPurchaseIds));
		when(similarUserRepository.getSimilarUsersByUserId(userId)).thenReturn(createSimilarUsers(similarUserIds));
		when(userRepository.getShoppingCartsByUserId(similarUserIds.get(0))).thenReturn(simiU);
		when(productRepository.findByProductId(any())).thenReturn(prod);

		List<ProductEntity> recommendedProducts = recService.getProductsBySimilarUserCarts(userId);

		assertTrue(recommendedProducts.size() > 0);
	}

	private List<ShoppingCartEntity> createShoppingCarts(List<Long> productIds) {
		List<ShoppingCartEntity> shoppingCarts = new ArrayList<>();
		ShoppingCartEntity cart = new ShoppingCartEntity();

		List<ProductCartEntity> productCarts = productIds.stream().map(productId -> {
			ProductEntity product = new ProductEntity();
			product.setProductId(productId);
			ProductCartEntity productCart = new ProductCartEntity();
			productCart.setProduct(product);
			return productCart;
		}).collect(Collectors.toList());

		cart.setProductCartEntities(productCarts);
		shoppingCarts.add(cart);

		return shoppingCarts;
	}

	private List<SimilarUserEntity> createSimilarUsers(List<Long> userIds) {
		List<SimilarUserEntity> similarUsers = new ArrayList<>();
		for (Long userId : userIds) {
			SimilarUserEntity similarUser = new SimilarUserEntity();

			UserEntity user1 = new UserEntity();
			user1.setUserId(userId);
			similarUser.setUser1(user1);

			UserEntity user2 = new UserEntity();
			user2.setUserId(userId);
			similarUser.setUser2(user2);

			similarUser.setSimilarity(0.5);

			similarUsers.add(similarUser);
		}
		return similarUsers;
	}

	@Test
	public void testGetProductsBySimilarUserCarts_WithUserPurchasedProducts_ReturnsRecommendedProducts() {

		Long userId = 1L;
		List<Long> similarUserCart1ProductIds = Arrays.asList(3L, 4L);
		List<Long> similarUserCart2ProductIds = Arrays.asList(5L, 6L);
		ProductEntity product = new ProductEntity();
		product.setProductId(1L);

		when(userRepository.getShoppingCartsByUserId(any()))
				.thenReturn(createShoppingCartsComprado(similarUserCart1ProductIds))
				.thenReturn(createShoppingCarts(similarUserCart2ProductIds));
		List<ProductEntity> recommendedProducts = recService.getProductsBySimilarUserCarts(userId);

		assertEquals(0, recommendedProducts.size());
	}

	private List<ShoppingCartEntity> createShoppingCartsComprado(List<Long> productIds) {
		List<ShoppingCartEntity> shoppingCarts = new ArrayList<>();
		ShoppingCartEntity cart = new ShoppingCartEntity();
		List<ProductCartEntity> productCarts = productIds.stream().map(productId -> {
			ProductCartEntity productCart = new ProductCartEntity();
			ProductEntity product = new ProductEntity();
			product.setProductId(productId);
			productCart.setProduct(product);
			return productCart;
		}).collect(Collectors.toList());
		cart.setProductCartEntities(productCarts);
		shoppingCarts.add(cart);
		return shoppingCarts;
	}

	@Test
	void getProductsBySimilarUserCarts_EmptyList() {
		Long userId = 1L;
		UserEntity user = new UserEntity();
		user.setUserId(userId);

		List<ShoppingCartEntity> userPurchases = Arrays.asList(
				createShoppingCart(1L, Arrays.asList(createProductCart(4L))),
				createShoppingCart(2L, Arrays.asList(createProductCart(5L))));
		Mockito.when(userRepository.getShoppingCartsByUserId(any())).thenReturn(userPurchases);

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

	@Test
	public void testGetProductsBySimilarReviewUsers_MaxProducts() {
		int maxProducts = 5;

		int numProducts = maxProducts * 2;

		List<Long> productIds1 = generateProductIds(numProducts,
				Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)); 
		
		List<Integer> user1Ratings = generateRatings(numProducts);

		List<Integer> user2Ratings = generateRatings(numProducts);

		SimilarUserEntity similarUserEntity1 = createSimilarUserEntity_max(1L, 2L, 0.9, user1Ratings, user2Ratings,
				numProducts);
		SimilarUserEntity similarUserEntity2 = createSimilarUserEntity_max(2L, 1L, 0.9, user1Ratings, user2Ratings,
				numProducts);

		UserEntity user1 = createMockedUserWithReviews(1L, user1Ratings, productIds1);
		
		when(userRepository.findByUserId(any())).thenReturn(user1);

		List<SimilarUserEntity> similarUsersList = new ArrayList<>();
		similarUsersList.add(similarUserEntity1);
		similarUsersList.add(similarUserEntity2);

		when(similarUserRepository.getSimilarUsersByUserId(any())).thenReturn(similarUsersList);

		List<ProductEntity> recommendedProducts = recService.getProductsBySimilarReviewUsers(1L);

		assertEquals(maxProducts, recommendedProducts.size());
	}

	private List<Integer> generateRatings(int numProducts) {
		List<Integer> ratings = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < numProducts; i++) {
			int rating = random.nextInt(5) + 1;
			ratings.add(rating);
		}

		return ratings;
	}

	private SimilarUserEntity createSimilarUserEntity_max(Long userId1, Long userId2, double similarity,
			List<Integer> ratings1, List<Integer> ratings2, int numProducts) {
		List<Long> productIds1 = generateProductIds(numProducts);
		List<Long> productIds2 = generateProductIds(numProducts);

		UserEntity user1 = createMockedUserWithReviews2(userId1, ratings1, productIds1);
		UserEntity user2 = createMockedUserWithReviews2(userId2, ratings2, productIds2);

		SimilarUserEntity similarUserEntity = new SimilarUserEntity();
		similarUserEntity.setId(1L);
		similarUserEntity.setUser1(user1);
		similarUserEntity.setUser2(user2);
		similarUserEntity.setSimilarity(similarity);

		return similarUserEntity;
	}

	private List<Long> generateProductIds(int numProducts) {
		List<Long> productIds = new ArrayList<>();
		for (int i = 1; i <= numProducts; i++) {
			productIds.add((long) i);
		}
		return productIds;
	}

	@Test
	public void testGetProductsBySimilarReviewUsers() {
		List<Long> productIds = Arrays.asList(1L, 2L, 3L);
		List<Integer> user1Ratings = Arrays.asList(5, 4, 3);
		List<Integer> user2Ratings = Arrays.asList(4, 3, 5);
		UserEntity user1 = createMockedUserWithReviews(1L, user1Ratings, productIds);

		when(userRepository.findByUserId(any())).thenReturn(user1);
		when(similarUserRepository.getSimilarUsersByUserId(any()))
				.thenReturn(Arrays.asList(createSimilarUserEntity(1L, 2L, 0.9, user1Ratings, user2Ratings)));

		List<ProductEntity> recommendedProducts = recService.getProductsBySimilarReviewUsers(1L);

		assertEquals(recommendedProducts.size(), 2);
	}

	private SimilarUserEntity createSimilarUserEntity(Long userId1, Long userId2, double similarity,
			List<Integer> ratings1, List<Integer> ratings2) {
		int numProducts = ratings1.size();
		List<Long> productIds = generateProductIds(numProducts, Arrays.asList(1L, 2L, 3L));
		List<Long> productIds2 = generateProductIds(numProducts, Arrays.asList(1L, 2L, 3L));
		UserEntity user1 = createMockedUserWithReviews(userId1, ratings1, productIds);
		UserEntity user2 = createMockedUserWithReviews(userId2, ratings2, productIds2);

		SimilarUserEntity similarUserEntity = new SimilarUserEntity();
		similarUserEntity.setUser1(user1);
		similarUserEntity.setUser2(user2);
		similarUserEntity.setSimilarity(similarity);

		return similarUserEntity;
	}

	private List<Long> generateProductIds(int numProducts, List<Long> excludedProductIds) {
		List<Long> productIds = new ArrayList<>();
		for (int i = 0; i < numProducts; i++) {
			long productId = i + 1;
			while (excludedProductIds.contains(productId)) {
				productId += numProducts;
			}
			productIds.add(productId);
		}
		return productIds;
	}

	private UserEntity createMockedUserWithReviews(Long userId, List<Integer> ratings, List<Long> productIds) {
		UserEntity user = new UserEntity();
		user.setUserId(userId);

		List<ReviewEntity> reviews = new ArrayList<>();
		for (int i = 0; i < ratings.size(); i++) {
			ReviewEntity review = new ReviewEntity();
			review.setRatingId((long) (i + 1));
			review.setRating(ratings.get(i));

			ProductEntity product = new ProductEntity();
			product.setProductId(productIds.get(i));

			UserEntity productOwner = new UserEntity();
			productOwner.setUserId(userId + 100);
			product.setUser(productOwner);

			review.setProduct(product);

			reviews.add(review);
		}

		user.setReviews(reviews);

		return user;
	}

	@Test
	public void testFindSimilarProductsByTags_Success() throws Exception {
		Long productId = 1L;
		ProductEntity currentProduct = new ProductEntity();
		currentProduct.setProductId(productId);
		currentProduct.setTags(Arrays.asList("tag1", "tag2"));

		ProductEntity similarProduct1 = new ProductEntity();
		similarProduct1.setProductId(2L);
		similarProduct1.setTags(Arrays.asList("tag1", "tag3"));

		ProductEntity similarProduct2 = new ProductEntity();
		similarProduct2.setProductId(3L);
		similarProduct2.setTags(Arrays.asList("tag2", "tag4"));

		List<ProductEntity> similarProducts = Arrays.asList(similarProduct1, similarProduct2);

		when(productRepository.findById(productId)).thenReturn(Optional.of(currentProduct));
		when(productRepository.findByTagsIn(any())).thenReturn(similarProducts);

		List<ProductEntity> recommendedProducts = recService.findSimilarProductsByTags(1L);

		assertEquals(recommendedProducts.size(), 2);
	}

	@Test
	public void testExecuteDailySimilarityCalculation() {
		List<Long> productIds = Arrays.asList(1L, 2L, 3L);
		List<Long> similarUserIds = Arrays.asList(2L, 3L);
		List<Long> similarUsers = new ArrayList<>(Arrays.asList(5L, 6L));
		ProductEntity prod = new ProductEntity();
		prod.setProductId(5L);

		List<ShoppingCartEntity> simiU = createShoppingCarts(similarUsers);

		when(productCartRepository.findProductIdsByShoppingCarts(any())).thenReturn(productIds);

		when(similarUserRepository.findAll()).thenReturn(createSimilarUsers(similarUserIds));

		when(similarUserRepository.getSimilarUsersByUserId(any())).thenReturn(createSimilarUsers(similarUserIds));
		when(userRepository.getShoppingCartsByUserId(any())).thenReturn(simiU);

		recService.executeDailySimilarityCalculation();

		verify(similarUserRepository, times(2)).save(any(SimilarUserEntity.class));

	}

	@Test
	public void testCalcularPorcentajeSimilitud() {
		ProductEntity productA = new ProductEntity();
		productA.setTags(Arrays.asList("tag1", "tag2", "tag3"));

		ProductEntity productB = new ProductEntity();
		productB.setTags(Arrays.asList("tag2", "tag3", "tag4"));

		double similarityPercentage = recService.calcularPorcentajeSimilitud(productA, productB);

		Assertions.assertEquals(50.0, similarityPercentage);
	}

	@Test
	public void testCalcularSimiProductos() {
		List<ProductEntity> products = Arrays.asList(createMockProduct("product1", Arrays.asList("tag1", "tag2")),
				createMockProduct("product2", Arrays.asList("tag2", "tag3")),
				createMockProduct("product3", Arrays.asList("tag3", "tag4")));

		ProductRepository productRepository = mock(ProductRepository.class);
		when(productRepository.findAll()).thenReturn(products);

		SimilarProductRepository similarProductRepository = mock(SimilarProductRepository.class);

		RecommendationSystemServiceImpl recService = new RecommendationSystemServiceImpl(productRepository, null, null,
				null, similarProductRepository, null);

		recService.calcularSimiProductos();

		verify(similarProductRepository, times(2)).save(any(SimilarProductEntity.class));
	}

	private ProductEntity createMockProduct(String name, List<String> tags) {
		ProductEntity product = new ProductEntity();
		product.setName(name);
		product.setTags(tags);
		return product;
	}

	private UserEntity createUserEntities(Long userId) {
		UserEntity user = new UserEntity();
		user.setUserId(userId);
		return user;
	}

	@Test
	public void testCalculateSimilarity_BothUsersHaveCommonProducts() {
		List<ShoppingCartEntity> userPurchases = Arrays.asList(createShoppingCart(Arrays.asList(1L, 2L, 3L)));

		List<ShoppingCartEntity> otherUserPurchases = Arrays.asList(createShoppingCart(Arrays.asList(3L, 4L, 5L)));
		when(productCartRepository.findProductIdsByShoppingCarts(any())).thenReturn(Arrays.asList(1L, 2L, 3L))
				.thenReturn(Arrays.asList(3L, 4L, 5L));
		double similarity = recService.calculateSimilarity(userPurchases, otherUserPurchases);
		Assertions.assertEquals(0.2, similarity, 0.01);
	}

	@Test
	public void testCalculateSimilarity_NoCommonProducts() {
		List<ShoppingCartEntity> userPurchases = Arrays.asList(createShoppingCart(Arrays.asList(1L, 2L, 3L)));

		List<ShoppingCartEntity> otherUserPurchases = Arrays.asList(createShoppingCart(Arrays.asList(4L, 5L, 6L)));

		when(productCartRepository.findProductIdsByShoppingCarts(any())).thenReturn(Arrays.asList(1L, 2L, 3L))
				.thenReturn(Arrays.asList(4L, 5L, 6L));

		double similarity = recService.calculateSimilarity(userPurchases, otherUserPurchases);
		assertEquals(0.0, similarity, 0.01);
	}

	@Test
	public void testCalculateSimilarity_AllCommonProducts() {
		List<ShoppingCartEntity> userPurchases = Arrays.asList(createShoppingCart(Arrays.asList(1L, 2L, 3L)));

		List<ShoppingCartEntity> otherUserPurchases = Arrays.asList(createShoppingCart(Arrays.asList(1L, 2L, 3L)));
		when(productCartRepository.findProductIdsByShoppingCarts(any())).thenReturn(Arrays.asList(1L, 2L, 3L))
				.thenReturn(Arrays.asList(1L, 2L, 3L));

		double similarity = recService.calculateSimilarity(userPurchases, otherUserPurchases);

		assertEquals(1.0, similarity, 0.01);
	}

	@Test
	public void testCalculateSimilarity_OneUserHasNoCommonProducts() {
		List<ShoppingCartEntity> userPurchases = Arrays.asList(createShoppingCart(Arrays.asList(1L, 2L, 3L)));

		List<ShoppingCartEntity> otherUserPurchases = Arrays.asList(createShoppingCart(Arrays.asList(4L, 5L, 6L)));

		double similarity = recService.calculateSimilarity(userPurchases, otherUserPurchases);
		Assertions.assertEquals(0.0, similarity);
	}

	public static ShoppingCartEntity createShoppingCart(List<Long> productIds) {
		ShoppingCartEntity shoppingCart = new ShoppingCartEntity();
		List<ProductCartEntity> productCartEntities = new ArrayList<>();
		for (Long productId : productIds) {
			ProductEntity product = createMockProduct(productId);
			ProductCartEntity productCartEntity = new ProductCartEntity(product, shoppingCart, 1);
			productCartEntities.add(productCartEntity);
		}
		shoppingCart.setProductCartEntities(productCartEntities);
		return shoppingCart;
	}

	private static ProductEntity createMockProduct(Long productId) {
		ProductEntity product = new ProductEntity();
		product.setProductId(productId);
		return product;
	}

	@Test
	public void testSaveSimilarityAndRecommendations() {
		Long userId1 = 1L;
		Long userId2 = 2L;
		double similarity = 0.8;
		List<ProductEntity> recommendedProducts = new ArrayList<>();
		ProductEntity product1 = new ProductEntity();
		product1.setProductId(1L);
		ProductEntity product2 = new ProductEntity();
		product2.setProductId(2L);
		recommendedProducts.add(product1);
		recommendedProducts.add(product2);

		when(userRepository.findById(userId1)).thenReturn(Optional.of(mock(UserEntity.class)));
		when(userRepository.findById(userId2)).thenReturn(Optional.of(mock(UserEntity.class)));

		recService.saveSimilarityAndRecommendations(userId1, userId2, similarity, recommendedProducts);

		verify(similarProductRepository).save(any(SimilarProductEntity.class));
	}

	@Test
	public void testExecuteDailySimilarityCalculationReviews() {
		UserEntity user1 = createUserEntities(1L);
		UserEntity user2 = createUserEntities(2L);

		List<ReviewEntity> user1Reviews = Arrays.asList(createReviewEntity(1L, 1L, 5, "Comentario 1"),
				createReviewEntity(2L, 1L, 4, "Comentario 2"), createReviewEntity(3L, 1L, 3, "Comentario 3"));
		List<ReviewEntity> user2Reviews = Arrays.asList(createReviewEntity(1L, 2L, 4, "Comentario 4"),
				createReviewEntity(2L, 2L, 3, "Comentario 5"), createReviewEntity(3L, 2L, 5, "Comentario 6"));

		user1.setReviews(user1Reviews);
		user2.setReviews(user2Reviews);

		when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

		recService.executeDailySimilarityCalculationReviews();

		verify(similarUserRepository, times(1)).save(any(SimilarUserEntity.class));
	}

	private ReviewEntity createReviewEntity(Long productId, Long userId, int rating, String comment) {
		ReviewEntity reviewEntity = new ReviewEntity();

		ProductEntity product = new ProductEntity();
		product.setProductId(productId);
		reviewEntity.setProduct(product);

		UserEntity user = new UserEntity();
		user.setUserId(userId);
		reviewEntity.setUserEntity(user);
		reviewEntity.setRating(rating);
		reviewEntity.setComment(comment);

		return reviewEntity;
	}

	@Test
	public void testGetRecommendedProducts_NoSimilarUsers() throws NullDataException {
		Long userId = 1L;
		when(similarUserRepository.getSimilarUsersByUserId(userId)).thenReturn(Collections.emptyList());

		List<ProductEntity> recommendedProducts = recService.getRecommendedProducts(userId);

		assertTrue(recommendedProducts.isEmpty());
	}

	@Test
	public void testGetRecommendedProducts_SimilarUsersNoReviews() throws NullDataException {
		Long userId = 1L;
		List<SimilarUserEntity> similarUsers = new ArrayList<>();

		UserEntity user1 = createMockedUserWithReviews2(1L, Arrays.asList(5, 4, 3), Arrays.asList(1L, 2L, 3L));
		UserEntity user2 = createMockedUserWithReviews2(2L, Arrays.asList(4, 3, 5), Arrays.asList(1L, 2L, 3L));

		SimilarUserEntity similarUser = new SimilarUserEntity();
		similarUser.setUser1(user1);
		similarUser.setUser2(user2);

		similarUsers.add(similarUser);

		when(similarUserRepository.getSimilarUsersByUserId(userId)).thenReturn(similarUsers);
		when(userRepository.findByUserId(userId)).thenReturn(new UserEntity());
		when(userRepository.getShoppingCartsByUserId(1L)).thenReturn(new ArrayList<>());
		when(userRepository.findByUserId(1L))
				.thenReturn(createMockedUserWithReviews(1L, Arrays.asList(5, 4, 3), Arrays.asList(1L, 2L, 3L)));
		when(similarUserRepository.getSimilarUsersByUserId(1L)).thenReturn(new ArrayList<>());
		List<ProductEntity> recommendedProducts = recService.getRecommendedProducts(userId);

		assertTrue(recommendedProducts.isEmpty());
	}

	private UserEntity createMockedUserWithReviews2(Long userId, List<Integer> ratings, List<Long> productIds) {
		UserEntity user = new UserEntity();
		user.setUserId(userId);

		List<ReviewEntity> reviews = new ArrayList<>();

		if (ratings != null && productIds != null && ratings.size() == productIds.size()) {
			for (int i = 0; i < ratings.size(); i++) {
				ReviewEntity review = new ReviewEntity();
				review.setRatingId(productIds.get(i));
				review.setRating(ratings.get(i));
				ProductEntity product = new ProductEntity();
				product.setProductId(productIds.get(i));
				review.setProduct(product);

				reviews.add(review);
			}
		}
		user.setReviews(reviews);
		return user;
	}

	@Test
	public void testGetRecommendedProducts_NullUserId() {
		assertThrows(NullDataException.class, () -> {
			recService.getRecommendedProducts(null);
		});
	}

	@Test
	public void testFindSimilarProductsByTags_CurrentProductIsNull() {
		Long productId = 1L;

		when(productRepository.findById(any())).thenReturn(Optional.empty());

		List<ProductEntity> similarProducts = recService.findSimilarProductsByTags(productId);

		assertEquals(null, similarProducts);
	}

	@Test
	void testCalculatePearsonCorrelation_NullReviews() {

		UserEntity user1 = new UserEntity();

		UserEntity user2 = new UserEntity();
		double correlation = recService.calculatePearsonCorrelation(user1, user2);

		assertEquals(0.0, correlation, 0.0001);

	}

	@Test
	void testGetProductsBySimilarReviewUsers_purchased() throws NullDataException {
		Long userId = 1L;
		ProductEntity product1 = new ProductEntity();
		product1.setProductId(1L);
		ReviewEntity review1 = new ReviewEntity();
		review1.setProduct(product1);

		ProductEntity product2 = new ProductEntity();
		product2.setProductId(2L);
		ReviewEntity review2 = new ReviewEntity();
		review2.setProduct(product2);

		List<ReviewEntity> reviews = new ArrayList<>();
		reviews.add(review1);
		reviews.add(review2);

		UserEntity user = new UserEntity();
		user.setUserId(userId);
		user.setReviews(reviews);

		UserEntity user2 = new UserEntity();
		user2.setUserId(2L);
		user2.setReviews(reviews);

		when(userRepository.findByUserId(any())).thenReturn(user);
		List<SimilarUserEntity> similarUsers = new ArrayList<>();
		SimilarUserEntity similarUser = new SimilarUserEntity();
		similarUser.setUser1(user);
		similarUser.setUser2(user2);
		similarUser.setSimilarity(0.7);
		similarUsers.add(similarUser);
		when(similarUserRepository.getSimilarUsersByUserId(any())).thenReturn(similarUsers);
		when(shoRepo.countByUserIdAndProductId(any(), any())).thenReturn((long) 1);

		List<ProductEntity> recommendedProducts = recService.getProductsBySimilarReviewUsers(userId);

		assertNotNull(recommendedProducts);

	}

}
