package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.controller.ReviewController;
import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.entity.UserEntity;
import com.exception.NotFoundException;
import com.exception.UnauthorizedException;
import com.service.ProductsService;
import com.service.ReviewService;
import com.service.UserService;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ProductsService productService;

	@Mock
	private ReviewService reviewService;

	@Mock
	private UserService userService;

	@InjectMocks
	private ReviewController reviewController;
	@Mock
	private BindingResult bindingResult;

	@Mock
	private Model model;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
	}

	String userEmail = "test@example.com";
	Principal principal = new Principal() {
		@Override
		public String getName() {
			return userEmail;
		}
	};

	@Test
	public void testVerReviews() throws Exception {
		Long productId = 1L;

		List<ReviewEntity> list = new ArrayList<>();
		ReviewEntity rev = new ReviewEntity();
		rev.setRatingId(1L);
		list.add(rev);

		when(reviewService.getReviewsByProductId(productId)).thenReturn(list);
		MockHttpServletResponse response = mockMvc
				.perform(get("/reviews/").param("productId", String.valueOf(productId))).andExpect(status().isOk())
				.andExpect(view().name("reviews/listReview")).andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getForwardedUrl()).isEqualTo("reviews/listReview");

	}

	@Test
	public void testAddReview() throws Exception {
		Long productId = 1L;

		UserEntity user = new UserEntity();
		user.setUserId(1L);
		user.setEmail("test@example.com");
		ReviewEntity review = new ReviewEntity();
		review.setRatingId(1L);

		ProductEntity product = new ProductEntity();
		product.setProductId(productId);
		when(userService.findByEmail(any())).thenReturn(user);
		when(productService.findById(productId)).thenReturn(product);
		when(reviewService.addReview(any())).thenReturn(review);

		MockHttpServletResponse response = mockMvc
				.perform(post("/reviews/add").principal(principal).param("productId", String.valueOf(productId))
						.flashAttr("review", review).principal(principal))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/product/detail/" + productId))
				.andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND.value());
		assertThat(response.getRedirectedUrl()).isEqualTo("/product/detail/" + productId);

	}

	@Test
	public void testAddReview_unauthorized() throws Exception {
		Long productId = 1L;

		UserEntity user = new UserEntity();
		user.setUserId(1L);
		user.setEmail("test@example.com");
		ReviewEntity review = new ReviewEntity();
		review.setRatingId(1L);

		ProductEntity product = new ProductEntity();
		product.setProductId(productId);

		when(userService.findByEmail(any())).thenReturn(user);
		when(productService.findById(productId)).thenReturn(product);

		doThrow(new UnauthorizedException("Unauthorized")).when(reviewService).addReview(any());

		MockHttpServletResponse response = mockMvc
				.perform(post("/reviews/add").principal(principal).param("productId", String.valueOf(productId))
						.flashAttr("review", review).principal(principal))
				.andExpect(status().isOk()).andExpect(view().name("/error"))
				.andExpect(model().attributeExists("errorMessage")).andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getForwardedUrl()).isEqualTo("/error");
	}

	@Test
	public void testAddReview_NotFoundException() throws Exception {
		Long productId = 1L;

		UserEntity user = new UserEntity();
		user.setUserId(1L);
		user.setEmail("test@example.com");
		ReviewEntity review = new ReviewEntity();
		review.setRatingId(1L);

		ProductEntity product = new ProductEntity();
		product.setProductId(productId);

		when(userService.findByEmail(any())).thenReturn(user);
		when(productService.findById(productId)).thenReturn(product);

		doThrow(new NotFoundException("Product not found")).when(reviewService).addReview(any());

		MockHttpServletResponse response = mockMvc
				.perform(post("/reviews/add").principal(principal).param("productId", String.valueOf(productId))
						.flashAttr("review", review).principal(principal))
				.andExpect(status().isOk()).andExpect(view().name("/error"))
				.andExpect(model().attributeExists("errorMessage")).andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getForwardedUrl()).isEqualTo("/error");
	}

	@Test
	public void testDeleteReview() throws Exception {
		Long reviewId = 1L;
		Long productId = 2L;

		doNothing().when(reviewService).deleteReview(reviewId);

		MockHttpServletResponse response = mockMvc
				.perform(post("/reviews/delete/{id}", reviewId).param("productId", String.valueOf(productId)))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/product/detail/" + productId))
				.andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND.value());
		assertThat(response.getRedirectedUrl()).isEqualTo("/product/detail/" + productId);
	}

	@Test
	public void testDeleteReview_Exception() throws Exception {
		Long reviewId = 1L;
		Long productId = 2L;

		doThrow(new NotFoundException("Error deleting review")).when(reviewService).deleteReview(reviewId);

		MockHttpServletResponse response = mockMvc
				.perform(post("/reviews/delete/{id}", reviewId).param("productId", String.valueOf(productId)))
				.andExpect(status().isOk()).andExpect(view().name("/error"))
				.andExpect(model().attributeExists("errorMessage")).andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getForwardedUrl()).isEqualTo("/error");
	}

}
