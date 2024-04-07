package com.example.demo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.entity.FeaturedProductEntity;
import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.entity.UserEntity;
import com.entity.enums.RoleEnum;
import com.exception.NotFoundException;
import com.exception.NullDataException;
import com.exception.UpdateProductException;
import com.repository.FeaturedRepository;
import com.repository.ProductCartRepository;
import com.repository.ProductRepository;
import com.repository.ReviewRepository;
import com.service.impl.ProductsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

	@InjectMocks
	private ProductsServiceImpl productService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ReviewRepository reviewRepo;

	@Mock
	private ProductCartRepository productCartRepository;

	@Mock
	private FeaturedRepository favRepo;

	@BeforeEach
	public void init() {
		MockitoAnnotations.openMocks(this);
		productService = new ProductsServiceImpl(productRepository, productCartRepository, reviewRepo, favRepo);
	}

	@Test
	void findById_ProductExists_ReturnsProductEntity() throws Exception {

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

		Mockito.when(productRepository.findByProductId(productId)).thenReturn(mockedProduct);

		ProductEntity result = productService.findById(productId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(productId, result.getProductId());
		Assertions.assertEquals("product", result.getName());
		Assertions.assertEquals("details", result.getDetail());
		Assertions.assertEquals(20.0, result.getPrice());
		Assertions.assertEquals(10, result.getStock());
	}

	@Test
	void findById_ProductNotExists_ThrowsNotFoundException() {

		Long productId = 1L;
		Mockito.when(productRepository.findByProductId(any())).thenReturn(null);

		NotFoundException result = assertThrows(NotFoundException.class, () -> productService.findById(productId));
		Assertions.assertEquals(result.getMessage(), "Product not found");
	}

	@Test
	void addProduct_ValidProduct_ProductSavedSuccessfully() throws Exception {

		ProductEntity productToAdd = new ProductEntity();
		productToAdd.setName("New Product");
		productToAdd.setDetail("Product Details");
		productToAdd.setPrice(30.0);
		productToAdd.setStock(15);

		Mockito.when(productRepository.save(productToAdd)).thenReturn(productToAdd);

		productService.addProduct(productToAdd);

		ProductEntity savedProduct = productRepository.save(productToAdd);
		Assertions.assertNotNull(savedProduct);
		Assertions.assertEquals(productToAdd, savedProduct);
		Assertions.assertEquals("New Product", savedProduct.getName());
		Assertions.assertEquals("Product Details", savedProduct.getDetail());
		Assertions.assertEquals(30.0, savedProduct.getPrice());
		Assertions.assertEquals(15, savedProduct.getStock());
	}

	@Test
	void getProducts_UserWithPublishedProducts_ReturnsProductList() {

		Long userId = 1L;
		List<ProductEntity> mockedProducts = createMockedProductList();
		Mockito.when(productRepository.findByUserUserId(userId)).thenReturn(mockedProducts);

		List<ProductEntity> result = productService.getProducts(userId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(mockedProducts.size(), result.size());
	}

	@Test
	void getProducts_UserWithoutPublishedProducts_ThrowsNotFoundException() {

		Long userId = 1L;
		Mockito.when(productRepository.findByUserUserId(userId)).thenReturn(null);

		NotFoundException result = Assertions.assertThrows(NotFoundException.class,
				() -> productService.getProducts(userId));

		Assertions.assertEquals("This user does not have any published products", result.getMessage());
	}

	@Test
	void getProductsExceptOwn_UserWithPublishedProducts_ReturnsProductList() {

		Long userId = 1L;
		List<ProductEntity> mockedProducts = createMockedProductList();
		Mockito.when(productRepository.findAllByUserUserIdIsNot(userId)).thenReturn(mockedProducts);

		List<ProductEntity> result = productService.getProductsExceptOwn(userId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(mockedProducts.size(), result.size());
	}

	@Test
	void getProductsExceptOwn_NoPublishedProducts_ThrowsNotFoundException() {

		Long userId = 1L;
		Mockito.when(productRepository.findAllByUserUserIdIsNot(userId)).thenReturn(null);

		NotFoundException result = Assertions.assertThrows(NotFoundException.class,
				() -> productService.getProductsExceptOwn(userId));

		Assertions.assertEquals("There are no products published yet", result.getMessage());
	}

	@Test
	void findByName_ProductExists_ReturnsProductEntity() throws Exception {

		String productName = "testProduct";
		ProductEntity mockedProduct = new ProductEntity();
		mockedProduct.setName(productName);

		Mockito.when(productRepository.findByName(productName)).thenReturn(mockedProduct);

		ProductEntity result = productService.findByName(productName);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(productName, result.getName());

	}

	@Test
	void findByName_ProductNotExists_ThrowsException() {

		String productName = "nonExistentProduct";
		Mockito.when(productRepository.findByName(productName)).thenReturn(null);

		Exception result = assertThrows(Exception.class, () -> productService.findByName(productName));
		Assertions.assertEquals("Query does not found results", result.getMessage());
	}

	@Test
	void deleteProduct_ProductExists_DeletesProduct() throws NotFoundException, IllegalArgumentException {

		Long productId = 1L;
		Mockito.doNothing().when(productRepository).deleteById(productId);

		productService.deleteProduct(productId);

		Mockito.verify(productRepository, Mockito.times(1)).deleteById(productId);
	}

	@Test
	void deleteProduct_ProductRepositoryThrowsException_ThrowsRuntimeException() {
		Long productId = 1L;

		Mockito.doThrow(RuntimeException.class).when(productRepository).deleteById(any());

		RuntimeException result = assertThrows(RuntimeException.class, () -> productService.deleteProduct(productId));
		Assertions.assertEquals("Failed to delete product", result.getMessage());
	}

	@Test
	void deleteProduct_ProductIdIsNull_ThrowsIllegalArgumentException() {

		Long productId = null;
		IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
				() -> productService.deleteProduct(productId));
		Assertions.assertEquals("ProductId cannot be null", result.getMessage());
	}

	@Test
	void getProducts_UserHasPublishedProducts_ReturnsProductList() {

		Long userId = 1L;
		List<ProductEntity> mockedProducts = new ArrayList<>();
		mockedProducts.add(new ProductEntity());
		mockedProducts.add(new ProductEntity());

		Mockito.when(productRepository.findByUserUserId(userId)).thenReturn(mockedProducts);

		List<ProductEntity> result = productService.getProducts(userId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(2, result.size());
	}

	@Test
	void getProducts_UserHasNoPublishedProducts_ThrowsNotFoundException() {

		Long userId = 1L;

		Mockito.when(productRepository.findByUserUserId(userId)).thenReturn(null);

		NotFoundException result = assertThrows(NotFoundException.class, () -> productService.getProducts(userId));
		Assertions.assertEquals("This user does not have any published products", result.getMessage());
	}

	@Test
	void getProductsExceptOwn_UserHasPublishedProducts_ReturnsProductList() {

		Long userId = 1L;

		List<ProductEntity> mockedProducts = createMockedProductList();

		Mockito.when(productRepository.findAllByUserUserIdIsNot(any())).thenReturn(mockedProducts);

		List<ProductEntity> result = productService.getProductsExceptOwn(userId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.size());
	}

	@Test
	void getProductsExceptOwn_UserHasNoPublishedProducts_ThrowsNotFoundException() {

		Long userId = 1L;

		Mockito.when(productRepository.findAllByUserUserIdIsNot(userId)).thenReturn(null);

		NotFoundException result = assertThrows(NotFoundException.class,
				() -> productService.getProductsExceptOwn(userId));
		Assertions.assertEquals("There are no products published yet", result.getMessage());
	}

	@Test
	void calculateAverageRating_ProductHasReviews_ReturnsAverageRating() {
		Long productId = 1L;
		List<ReviewEntity> reviews = createMockedReviews();
		Mockito.when(reviewRepo.findByProductProductId(any())).thenReturn(reviews);
		double result = productService.calculateAverageRating(productId);
		Assertions.assertEquals(4.0, result, 0.01);
	}

	@Test
	void calculateAverageRating_ProductHasNoReviews_ReturnsZero() {

		Long productId = 1L;
		Mockito.when(reviewRepo.findByProductProductId(any())).thenReturn(null);

		double result = productService.calculateAverageRating(productId);
		Assertions.assertEquals(0.0, result, 0.01);
	}

	@Test
	void updateProduct_ValidInput_ReturnsTrue() throws UpdateProductException {

		ProductEntity product = createMockedProduct();
		ProductEntity editedProduct = createMockedProduct();
		editedProduct.setDetail("edited");
		editedProduct.setName("ed");
		Mockito.when(productRepository.save(any())).thenReturn(editedProduct);
		boolean result = productService.updateProduct(product, editedProduct);

		Assertions.assertTrue(result);
		Assertions.assertEquals("ed", product.getName());
		Assertions.assertEquals("edited", product.getDetail());
	}

	@Test
	void updateProduct_InvalidProductId_ThrowsUpdateProductException() {

		ProductEntity product = createMockedProduct();
		ProductEntity editedProduct = createMockedProduct();
		editedProduct.setDetail("edited");
		editedProduct.setName("ed");

		UpdateProductException result = Assertions.assertThrows(UpdateProductException.class,
				() -> productService.updateProduct(product, editedProduct));

		Assertions.assertEquals("Error al actualizar el producto", result.getMessage());
	}

	@Test
	void updateProduct_InvalidProductId_Illegal() {

		ProductEntity product = createMockedProduct();
		product.setProductId(null);
		ProductEntity editedProduct = createMockedProduct();
		editedProduct.setDetail("edited");
		editedProduct.setName("ed");

		IllegalArgumentException result = Assertions.assertThrows(IllegalArgumentException.class,
				() -> productService.updateProduct(product, editedProduct));

		Assertions.assertEquals("El identificador del producto no puede ser nulo para la actualizacion",
				result.getMessage());
	}

	@Test
	void searchProducts_ValidSearchTerm_ReturnsProductList() {

		List<ProductEntity> mockedProducts = createMockedProductList();
		Mockito.when(productRepository.findByNameContainingIgnoreCase(any())).thenReturn(mockedProducts);

		List<ProductEntity> result = productService.searchProducts(any());

		Assertions.assertNotNull(result);
		Assertions.assertEquals(mockedProducts.size(), result.size());
	}

	@Test
	void searchProducts_NoResultsFound_ThrowsRuntimeException() {

		String searchTerm = "nonexistentTerm";
		Mockito.when(productRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(null);

		RuntimeException result = assertThrows(RuntimeException.class, () -> productService.searchProducts(searchTerm));
		Assertions.assertEquals("No results found", result.getMessage());
	}

	@Test
	void getSoldProducts_ValidUserId_ReturnsSoldProductList() {

		Long userId = 1L;
		List<ProductEntity> mockedProducts = createMockedProductList();
		Mockito.when(productRepository.findByUserUserIdAndSoldTrue(userId)).thenReturn(mockedProducts);

		List<ProductEntity> result = productService.getSoldProducts(userId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(mockedProducts.size(), result.size());
	}

	@Test
	void getPurchasedProducts_ValidUserId_ReturnsPurchasedProductList() throws NullDataException {
  
		Long userId = 1L;
		List<ProductEntity> mockedProducts = createMockedProductList();
		Mockito.when(productCartRepository.getPurchasedProductsByUserId(userId)).thenReturn(mockedProducts);

		List<ProductEntity> result = productService.getPurchasedProducts(userId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(mockedProducts.size(), result.size());
	}

	@Test
	void getPurchasedProducts_NullUserId_ThrowsNullDataException() {

		Long userId = null;

		NullDataException result = assertThrows(NullDataException.class,
				() -> productService.getPurchasedProducts(userId));
		Assertions.assertEquals("el campo de entrada userId no puede ser null", result.getMessage());
	}

	@Test
	void testIncreaseNumOfViews() {

		ProductEntity mockedProduct = createMockedProduct();
		mockedProduct.setNumOfViews(5);

		ProductEntity result = productService.increaseNumOfViews(mockedProduct);

		assertEquals(6, result.getNumOfViews());

	}

	@Test
	void checkIfProductIsFavorite_ProductIsFavorite_ReturnsTrue() {

		Long productId = 1L;
		ProductEntity mockedProduct = createMockedProduct();
		FeaturedProductEntity featuredProduct = new FeaturedProductEntity();
		featuredProduct.setFeaturedId(1L);
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
		featuredProduct.setUser(user);
		featuredProduct.setProduct(mockedProduct);
		Mockito.when(productRepository.findByProductId(any())).thenReturn(mockedProduct);
		List<FeaturedProductEntity> favs = new ArrayList<>();
		favs.add(featuredProduct);
		Mockito.when(favRepo.getFeaturedProductsByProductId(any())).thenReturn(favs);
		boolean result = productService.checkIfProductIsFavorite(productId);

		Assertions.assertTrue(result);
	}

	@Test
	void checkIfProductIsFavorite_ProductNotFound_ReturnsFalse() {
		Long productId = 1L;
		Mockito.when(productRepository.findByProductId(any())).thenReturn(null);
		boolean result = productService.checkIfProductIsFavorite(productId);

		Assertions.assertFalse(result);
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

	private List<ReviewEntity> createMockedReviews() {
		List<ReviewEntity> reviews = new ArrayList<>();

		ReviewEntity rev1 = new ReviewEntity();
		rev1.setRatingId(1L);
		rev1.setComment("1");
		rev1.setRating(3);
		reviews.add(rev1);

		ReviewEntity rev2 = new ReviewEntity();
		rev2.setRatingId(2L);
		rev2.setComment("2");
		rev2.setRating(4);
		reviews.add(rev2);

		ReviewEntity rev3 = new ReviewEntity();
		rev3.setRatingId(3L);
		rev3.setComment("3");
		rev3.setRating(5);
		reviews.add(rev3);

		return reviews;
	}

	private List<ProductEntity> createMockedProductList() {
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
		List<ProductEntity> mockedProducts = new ArrayList<>();
		mockedProducts.add(mockedProduct);
		return mockedProducts;
	}
}
