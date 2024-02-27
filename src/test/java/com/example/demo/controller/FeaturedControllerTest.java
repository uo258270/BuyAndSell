package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import com.controller.FeaturedController;
import com.entity.FeaturedProductEntity;
import com.entity.ProductEntity;
import com.entity.UserEntity;
import com.service.FeaturedProductService;
import com.service.ProductService;
import com.service.ShoppingCartService;
import com.service.UserService;

@ExtendWith(MockitoExtension.class)
public class FeaturedControllerTest {

	private MockMvc mockMvc;

	@Mock
    private FeaturedProductService featuredService;

	@Mock
	private UserService userService;
	
	@Mock
	private ShoppingCartService cartService;

	@Mock
    private ProductService productService;

	@Mock
	private Model model;

	@InjectMocks
	private FeaturedController featuredController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(featuredController).build();
	}
	
	String userEmail = "test@example.com";
	Principal principal = new Principal() {
		@Override
		public String getName() {
			return userEmail;
		}
	};

	 @Test
	    public void testAddFavProduct_ProductExists() throws Exception {
	       
	        ProductEntity product = new ProductEntity();
	        product.setProductId(1L);
	        product.setName("Test Product");

	       
	        UserEntity user = new UserEntity();
	        user.setUserId(1L);
	        user.setEmail("test@example.com");

	        FeaturedProductEntity featuredProduct = new FeaturedProductEntity();
	        featuredProduct.setFeaturedId(1L);
	        featuredProduct.setProduct(product);
	        featuredProduct.setUser(user); 

	        when(userService.findByEmail(any())).thenReturn(user);

	        doNothing().when(featuredService).addFeatured(any(ProductEntity.class), any(UserEntity.class));

		    List<FeaturedProductEntity> list = new ArrayList<>();
		    Mockito.when(featuredService.findByUser(user)).thenReturn(list);

	        MvcResult result = mockMvc.perform(post("/featured/add").principal(principal)
	                .param("id", "1")
	                .param("name", "Test Product")
	                .flashAttr("prod", product)
	                .flashAttr("user", user))
	            .andReturn();

	        assertEquals("redirect:/featured/listByUser", result.getModelAndView().getViewName());
	        
	    }

	
	@Test
	void addFavProduct_Failure() throws Exception {
		
	    ProductEntity product = new ProductEntity();
	    product.setProductId(1L);
	    product.setName("Test Product");

	    UserEntity user = new UserEntity();
	    user.setUserId(1L);
	    user.setEmail("test@example.com");

	    when(userService.findByEmail(any())).thenReturn(user);

	    
	    doThrow(new Exception("Product already in favorites")).when(featuredService).addFeatured(any(), any());

	    MockHttpServletResponse response = mockMvc.perform(post("/featured/add").principal(principal)
	            .param("id", "1")
	            .param("name", "Test Product")
	            .flashAttr("prod", product)
	            .flashAttr("user", user))
	        .andReturn()
	        .getResponse();

	    assertEquals(HttpStatus.OK.value(), response.getStatus(), "Status code must be OK");
	    assertEquals("/error", response.getForwardedUrl(), "Should forward to /error page");

	}

	@Test
	void deleteFavProduct_Success() throws Exception {
		
		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn("test@example.com");
		UserEntity user = new UserEntity();
		when(userService.findByEmail(any())).thenReturn(user);
		FeaturedProductEntity featuredProduct = new FeaturedProductEntity();
		when(featuredService.getFeaturedById(any())).thenReturn(featuredProduct);

		
		String result = featuredController.deleteFavProduct(model, principal, 1L);

		assertEquals("/featured/listByUser", result);
		verify(featuredService, times(1)).deleteFeaturedProduct(featuredProduct);
		verify(model, times(2)).addAttribute(any(), any());
	}

	@Test
	void deleteFavProduct_Failure() throws Exception {
	
		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn("test@example.com");
		UserEntity user = new UserEntity();
		when(userService.findByEmail(any())).thenReturn(user);
		FeaturedProductEntity featuredProduct = new FeaturedProductEntity();
		when(featuredService.getFeaturedById(any())).thenReturn(featuredProduct);
		doThrow(Exception.class).when(featuredService).deleteFeaturedProduct(featuredProduct);

		
		assertThrows(Exception.class, () -> featuredController.deleteFavProduct(model, principal, 1L));
	}

	@Test
	void listFavProducts_Success() throws Exception {
		
		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn("test@example.com");
		UserEntity user = new UserEntity();
		when(userService.findByEmail(any())).thenReturn(user);
		List<FeaturedProductEntity> featuredList = new ArrayList<>();
		when(featuredService.findByUser(user)).thenReturn(featuredList);

		String result = featuredController.listFavProducts(model, principal);

		assertEquals("featured/listByUser", result);
		verify(model, times(2)).addAttribute(any(), any());
	}

	@Test
    void checkIfProductIsFavorite() {
      
        when(productService.checkIfProductIsFavorite(anyLong())).thenReturn(true);

       
        boolean result = featuredController.checkIfProductIsFavorite(1L);

        assertTrue(result);
        verify(productService, times(1)).checkIfProductIsFavorite(1L);
    }

}
