package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.controller.ProductController;
import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.entity.UserEntity;
import com.entity.enums.CategoryEnum;
import com.entity.enums.RoleEnum;
import com.exception.NotFoundException;
import com.exception.UpdateProductException;
import com.service.ImageService;
import com.service.ProductService;
import com.service.RecommendationSystemService;
import com.service.UserService;
import com.validators.AddOfferValidator;

import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ProductService productService;

	@Mock
	private ImageService imageService;

	@Mock
	private UserService userService;

	@Mock
	private AddOfferValidator addOfferValidator;

	@Mock
	private RecommendationSystemService recommendedService;

	@InjectMocks
	private ProductController productController;
	@Mock
	private BindingResult bindingResult;

	@Mock
	private Model model;

	@Value("${image.upload.path}")
	private String uploadPath;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
	}

	String userEmail = "test@example.com";
	Principal principal = new Principal() {
		@Override
		public String getName() {
			return userEmail;
		}
	};

	@Test
	public void testListOwnProducts() throws Exception {
		String email = "test@example.com";
		UserEntity user = new UserEntity();
		user.setEmail(email);
		String userEmail = "test@example.com";
		Principal principal = new Principal() {
			@Override
			public String getName() {
				return userEmail;
			}
		};

		List<ProductEntity> products = new ArrayList<>();

		when(userService.findByEmail(email)).thenReturn(user);
		when(productService.getProducts(user.getUserId())).thenReturn(products);

		MockHttpServletResponse response = mockMvc.perform(get("/product/own").principal(principal))
				.andExpect(status().isOk()).andExpect(model().attributeExists("products"))
				.andExpect(view().name("product/listOwn")).andReturn().getResponse();
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getForwardedUrl()).isEqualTo("product/listOwn");
	}

	@Test
	public void testListOwnProductsWhenPrincipalIsNull() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(get("/product/own")).andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND.value()); // 302 - Redirección
		assertThat(response.getRedirectedUrl()).isEqualTo("/login");
	}

	@Test
	public void testListAllProductsExceptOwn() throws Exception {

		String email = "test@example.com";
		UserEntity user = new UserEntity();
		user.setEmail(email);

		String userEmail = "test@example.com";
		Principal principal = new Principal() {
			@Override
			public String getName() {
				return userEmail;
			}
		};

		List<ProductEntity> productsExceptOwn = new ArrayList<>();

		when(userService.findByEmail(email)).thenReturn(user);
		when(productService.getProductsExceptOwn(user.getUserId())).thenReturn(productsExceptOwn);

		MvcResult result = mockMvc.perform(get("/product/exceptOwn").principal(principal)).andReturn();

		assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getModelAndView().getViewName()).isEqualTo("product/listProducts");
		assertThat(result.getModelAndView().getModel().containsKey("products")).isTrue();
	}
 
	@Test
	public void testListAllRecommendedProductsWhenPrincipalIsNull() throws Exception {

		MockHttpServletResponse response = mockMvc.perform(get("/product/allRecommendedProducts")).andReturn()
				.getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND.value()); // 302 - Redirección
		assertThat(response.getRedirectedUrl()).isEqualTo("/login");
	}

	@Test
	public void testListAllRecommendedProducts() throws Exception {

		String email = "test@example.com";
		UserEntity user = new UserEntity();
		user.setEmail(email);
		user.setUserId(1L);
		user.setRole(RoleEnum.ROLE_USER);

		String userEmail = "test@example.com";
		Principal principal = new Principal() {
			@Override
			public String getName() {
				return userEmail;
			}
		};

		List<ProductEntity> recommendedByReview = Collections.singletonList(new ProductEntity());
		List<ProductEntity> recommendedByCart = Collections.singletonList(new ProductEntity());
		List<ProductEntity> popular = Collections.singletonList(new ProductEntity());
		List<ProductEntity> topRated = Collections.singletonList(new ProductEntity());

		when(userService.findByEmail(email)).thenReturn(user);
		when(recommendedService.getProductsBySimilarReviewUsers(any())).thenReturn(recommendedByReview);
		when(recommendedService.getProductsBySimilarUserCarts(any())).thenReturn(recommendedByCart);
		when(recommendedService.getMostPopularProducts()).thenReturn(popular);
		when(recommendedService.getTopRatedProducts()).thenReturn(topRated);

		MvcResult result = mockMvc.perform(get("/product/allRecommendedProducts").principal(principal)).andReturn();

		assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getModelAndView().getViewName()).isEqualTo("/product/listProducts");
		assertThat(result.getModelAndView().getModel().containsKey("products")).isTrue();

	}

	@Test
	void testGetProduct() throws Exception {

		MockitoAnnotations.openMocks(this);

		MvcResult result = mockMvc.perform(get("/product/add").principal(principal)).andReturn();

		assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getModelAndView().getViewName()).isEqualTo("product/add");
		assertThat(result.getModelAndView().getModel().containsKey("product")).isTrue();
		assertThat(result.getModelAndView().getModel().containsKey("categorias")).isTrue();
	}

	@Test
	void testSetProduct_ValidationErrors() throws Exception {

		MockitoAnnotations.openMocks(this);
		ProductEntity product = new ProductEntity();
		MockMultipartFile file = new MockMultipartFile("images", "image.jpg", "image/jpeg",
				"contenido_de_prueba".getBytes());

		product.setName("");
		product.setDetail("");

		doAnswer(invocation -> {
			BindingResult result = invocation.getArgument(1);
			result.rejectValue("name", "error.name");
			result.rejectValue("detail", "error.detail");
			return null;
		}).when(addOfferValidator).validate(any(ProductEntity.class), any(BindingResult.class));

		MvcResult result = mockMvc
				.perform(multipart("/product/add").file(file).principal(principal).flashAttr("product", product))
				.andReturn();

		assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(result.getModelAndView().getViewName()).isEqualTo("product/add");
		assertThat(result.getModelAndView().getModel().containsKey("errors")).isTrue();

	}

	@Test
	void testSetProduct_Success() throws Exception {

		MockitoAnnotations.openMocks(this);
		ProductEntity product = new ProductEntity();
		MockHttpServletResponse response = new MockHttpServletResponse();
		when(userService.findByEmail("test@example.com")).thenReturn(new UserEntity());
		when(imageService.saveImages(anyList())).thenReturn(Collections.singletonList("image.jpg"));

		MockMultipartFile file = new MockMultipartFile("images", "image.jpg", "image/jpeg",
				"contenido_de_prueba".getBytes());

		MvcResult result = mockMvc
				.perform(multipart("/product/add").file(file).principal(principal).flashAttr("product", product))
				.andReturn();
		response = result.getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND.value());
		assertThat(result.getModelAndView().getViewName()).isEqualTo("redirect:/product/own");
		verify(productService).addProduct(any(ProductEntity.class));
	}

	@Test
	void testDeleteProduct() throws Exception {
		Long productId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.get("/product/delete/{id}", productId))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/product/own"));

		verify(productService).deleteProduct(productId);
	}

	@Test
	void testEditProductForm() throws Exception {

		Long productId = 1L;
		ProductEntity mockProduct = new ProductEntity();
		mockProduct.setProductId(productId);
		mockProduct.setName("product");
		mockProduct.setDetail("detail");
		mockProduct.setCategory(CategoryEnum.DEPORTE);

		when(productService.findById(productId)).thenReturn(mockProduct);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/product/edit/{id}", productId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("product/editProduct"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("categorias", "product")).andReturn();

		ModelAndView modelAndView = result.getModelAndView();
		ModelMap modelMap = modelAndView.getModelMap();

		assertArrayEquals(CategoryEnum.values(), (CategoryEnum[]) modelMap.getAttribute("categorias"));

		assertEquals(mockProduct, modelMap.getAttribute("product"));
		assertEquals(modelAndView.getViewName(), "product/editProduct");
	}

	@Test
	void testEditProduct() throws Exception {

		Long productId = 1L;
		ProductEntity mockProduct = new ProductEntity();
		mockProduct.setProductId(productId);
		mockProduct.setName("Mock Product");
		mockProduct.setDetail("Mock Description");
		MockMultipartFile file = new MockMultipartFile("images", "image.jpg", "image/jpeg",
				"contenido_de_prueba".getBytes());

		when(productService.findById(productId)).thenReturn(mockProduct);
		when(imageService.saveImages(anyList())).thenReturn(Collections.singletonList("image.jpg"));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/product/edit/{id}", productId).file(file)
				.principal(principal).param("editedProduct.productId", String.valueOf(productId))
				.param("editedProduct.name", "Edited Product").param("editedProduct.detail", "Edited Description"))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/product/own")).andReturn();

		ModelAndView modelAndView = result.getModelAndView();

		assertEquals(modelAndView.getViewName(), "redirect:/product/own");

	}

	@Test
	void testEditProduct_NotFound() throws Exception {

		Long productId = 1L;
		when(productService.findById(any()))
				.thenThrow(new NotFoundException("El producto no se ha encontrado en el servicio"));

		assertThatThrownBy(() -> {
			mockMvc.perform(MockMvcRequestBuilders.multipart("/product/edit/{id}", productId)).andReturn();
		}).isInstanceOf(ServletException.class).hasCauseInstanceOf(NotFoundException.class).extracting("cause")
				.satisfies(cause -> assertThat(((Throwable) cause).getMessage())
						.isEqualTo("El producto no se ha encontrado en el servicio"));
	}

	@Test
	void testEditProduct_UpdateProdutError() throws Exception {

		Long productId = 1L;
		ProductEntity mockProduct = new ProductEntity();
		mockProduct.setProductId(productId);
		mockProduct.setName("Mock Product");
		mockProduct.setDetail("Mock Description");

		MockMultipartFile file = new MockMultipartFile("images", "image.jpg", "image/jpeg",
				"contenido_de_prueba".getBytes());

		when(productService.findById(productId)).thenReturn(mockProduct);
		when(imageService.saveImages(anyList())).thenReturn(Collections.singletonList("image.jpg"));

		when(productService.updateProduct(any(), any()))
				.thenThrow(new UpdateProductException("Error al actualizar el producto"));

		assertThatThrownBy(() -> {
			mockMvc.perform(MockMvcRequestBuilders.multipart("/product/edit/{id}", productId).file(file)
					.param("imagesToRemove", "imageToRemove.jpg")).andReturn();
		}).isInstanceOf(ServletException.class).hasCauseInstanceOf(UpdateProductException.class).extracting("cause")
				.satisfies(cause -> assertThat(((Throwable) cause).getMessage())
						.isEqualTo("Error al actualizar el producto"));
	}

	@Test
	void testShowProductDetail_ProductFound() throws Exception {
		Long productId = 1L;
		ProductEntity product = new ProductEntity();
		product.setProductId(productId);
		product.setName("Product Name");
		product.setDetail("Product Description");
		product.setPrice(12.99);

		List<ReviewEntity> reviews = new ArrayList<>();
		ReviewEntity review = new ReviewEntity();
		review.setRatingId(1L);
		review.setRating(5);
		review.setComment("Great product!");
		reviews.add(review);
		product.setReviews(reviews);
		when(productService.findById(any())).thenReturn(product);
		
		
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.get("/product/detail/{productId}", productId).principal(principal))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("product/detail"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("product"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("principal"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("averageRating"))
				.andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("errors")).andReturn();

		product = (ProductEntity) result.getModelAndView().getModel().get("product");

		Assertions.assertEquals(productId, product.getProductId());
		Assertions.assertEquals("Product Name", product.getName());
		Assertions.assertEquals(12.99, product.getPrice());
		Assertions.assertEquals("Product Description", product.getDetail());
		Assertions.assertEquals(5.0, product.getReviews().get(0).getRating());
	}

	@Test
	void testShowProductDetail_ProductNotFound() throws Exception {
		Long productId = 1L;

		when(productService.findById(productId)).thenThrow(new NotFoundException("El producto no existe"));

		assertThatThrownBy(() -> {
			mockMvc.perform(MockMvcRequestBuilders.get("/product/detail/{productId}", productId).principal(principal))
					.andReturn();
		}).isInstanceOf(ServletException.class).hasCauseInstanceOf(NotFoundException.class).extracting("cause")
				.satisfies(cause -> assertThat(((Throwable) cause).getMessage()).isEqualTo("El producto no existe"));

	}

	@Test
	void testShowSearchPage() throws Exception {
		mockMvc.perform(get("/product/search")).andExpect(status().isOk()).andExpect(view().name("product/search"))
				.andExpect(model().size(0));
	}

	@Test
	void testSearchProducts_NoSearchTerm() throws Exception {
		mockMvc.perform(get("/product/search/results")).andExpect(status().isOk())
				.andExpect(view().name("product/searchResults")).andExpect(model().attributeDoesNotExist("searchTerm"))
				.andExpect(model().attributeDoesNotExist("searchResults"));
	}

	@Test
	void testSearchProducts_WithSearchTerm() throws Exception {
		String searchTerm = "example";
		ProductEntity product = new ProductEntity();
		product.setProductId(1L);
		product.setDetail("1");
		product.setName("example");

		List<ProductEntity> mockSearchResults = Arrays.asList(product);

		when(productService.searchProducts(searchTerm)).thenReturn(mockSearchResults);

		mockMvc.perform(get("/product/search/results").param("searchTerm", searchTerm)).andExpect(status().isOk())
				.andExpect(view().name("product/searchResults")).andExpect(model().attribute("searchTerm", searchTerm))
				.andExpect(model().attribute("searchResults", mockSearchResults));
	}

	@Test
	void testListPurchasedProducts() throws Exception {

		String userEmail = "user@example.com";
		Principal principal = () -> userEmail;

		ProductEntity product = new ProductEntity();
		product.setProductId(1L);
		product.setDetail("1");
		product.setName("example");

		UserEntity mockUser = new UserEntity();
		mockUser.setUserId(1L);
		mockUser.setEmail(userEmail);

		List<ProductEntity> mockPurchasedProducts = Arrays.asList(product);

		when(userService.findByEmail(userEmail)).thenReturn(mockUser);
		when(productService.getPurchasedProducts(mockUser.getUserId())).thenReturn(mockPurchasedProducts);

		MvcResult result = mockMvc.perform(get("/product/purchased").principal(principal)).andExpect(status().isOk())
				.andExpect(view().name("product/listPurchased")).andReturn();

		ModelMap modelMap = result.getModelAndView().getModelMap();
		List<ProductEntity> list = (List<ProductEntity>) modelMap.get("purchasedProducts");
		List<ProductEntity> purchasedProducts = list;

		Assertions.assertNotNull(purchasedProducts, "La lista de productos comprados no debe ser nula");
		Assertions.assertEquals(mockPurchasedProducts.size(), purchasedProducts.size(),
				"La cantidad de productos debe ser la misma");
	}


}
