package com.example.demo.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import com.controller.ShoppingCartController;
import com.entity.ShoppingCartEntity;
import com.exception.InvalidStockException;
import com.exception.NotEnoughMoney;
import com.exception.ProductAlreadySoldException;
import com.service.ProductsService;
import com.service.ShoppingCartService;
import com.service.UserService;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartControllerTest {

	private MockMvc mockMvc;

	@Mock
	ShoppingCartService shoppingCartService;

	@Mock
	UserService userService;

	@Mock
	ProductsService productService;

	@Mock
	private Model model;

	@InjectMocks
	private ShoppingCartController cartController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
	}

	String userEmail = "test@example.com";
	Principal principal = new Principal() {
		@Override
		public String getName() {
			return userEmail;
		}
	};

	@Test
	public void testAddProduct_ok() throws Exception {

		Long productId = 1L;
		int quantity = 2;
		ShoppingCartEntity mockShoppingCartEntity = new ShoppingCartEntity();
		when(shoppingCartService.incrementProductQuantity(any())).thenReturn(mockShoppingCartEntity);

		MvcResult result = mockMvc
				.perform(post("/cart/addProduct").param("productId", productId.toString()).param("quantity",
						String.valueOf(quantity)))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/cart")).andReturn();

		assertEquals(null, result.getFlashMap().get("aviso"));
	}

	@Test
	public void testAddProduct_ko() throws Exception {

		Long productId = 1L;
		int quantity = 2;
		when(shoppingCartService.incrementProductQuantity(any())).thenThrow(new InvalidStockException(userEmail));

		MvcResult result = mockMvc
				.perform(post("/cart/addProduct").param("productId", productId.toString()).param("quantity",
						String.valueOf(quantity)))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/cart")).andReturn();

		assertEquals("No hay suficiente stock", result.getFlashMap().get("aviso"));
	}

	@Test
	public void testClearCart() throws Exception {
		ShoppingCartEntity shop = new ShoppingCartEntity();
		when(shoppingCartService.clear()).thenReturn(shop);

		mockMvc.perform(post("/cart/clearCart")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/cart"));

		mockMvc.perform(get("/cart")).andExpect(status().isOk()).andExpect(model().attributeExists("cartCleared"));
	}

	@Test
	public void testGetCartSuccess() throws Exception {
	
		ShoppingCartEntity cart = new ShoppingCartEntity();
		when(shoppingCartService.getCart()).thenReturn(cart);

		MvcResult result = mockMvc.perform(get("/cart")).andExpect(status().isOk())
				.andExpect(model().attributeExists("cart")).andReturn();

		assertEquals(cart, result.getModelAndView().getModel().get("cart"));
	}

	@Test
	public void testGetCartException() throws Exception {

		doThrow(RuntimeException.class).when(shoppingCartService).getCart();

		mockMvc.perform(get("/cart")).andExpect(status().isOk()).andExpect(model().attributeExists("errorMessage"))
				.andExpect(model().attribute("errorMessage", "No se pudo cargar el carrito"))
				.andExpect(view().name("/error"));
	}

	@Test
	public void testBuyProductSuccess() throws Exception {

		ShoppingCartEntity cart = new ShoppingCartEntity();
		when(shoppingCartService.buyShoppingCart(principal.getName())).thenReturn(cart);

		MvcResult result = mockMvc.perform(post("/cart/buy").principal(principal)).andExpect(status().isOk())
				.andReturn();

		assertEquals("/cart/thankYou", result.getResponse().getForwardedUrl());
	}

	@Test
	public void testBuyProductNotEnoughMoneyException() throws Exception {

		doThrow(NotEnoughMoney.class).when(shoppingCartService).buyShoppingCart(principal.getName());

		mockMvc.perform(post("/cart/buy").principal(principal)).andExpect(status().isOk())
				.andExpect(model().attributeExists("errorMessage"))
				.andExpect(model().attribute("errorMessage", "No tienes suficiente dinero para realizar la compra."));
	}

	@Test
	public void testBuyProductProductAlreadySoldException() throws Exception {

		doThrow(ProductAlreadySoldException.class).when(shoppingCartService).buyShoppingCart(principal.getName());

		mockMvc.perform(post("/cart/buy").principal(principal)).andExpect(status().isOk())
				.andExpect(model().attributeExists("errorMessage"))
				.andExpect(model().attribute("errorMessage", "El producto ya ha sido vendido."));
	}

	@Test
	void testUpdateQuantity_Ok() throws Exception {

		Long productId = 1L;
		int quantity = 2;
		String action = "increment";
		ShoppingCartEntity cart = new ShoppingCartEntity();
		when(shoppingCartService.incrementProductQuantity(productId)).thenReturn(cart);

		mockMvc.perform(post("/cart/updateQuantity").param("productId", productId.toString())
				.param("quantity", String.valueOf(quantity)).param("action", action))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/cart"));
	}

	@Test
	void testUpdateQuantity_InvalidStockException() throws Exception {

		Long productId = 1L;
		int quantity = 2;
		String action = "increment";
		doThrow(InvalidStockException.class).when(shoppingCartService).incrementProductQuantity(productId);

		mockMvc.perform(post("/cart/updateQuantity").param("productId", productId.toString())
				.param("quantity", String.valueOf(quantity)).param("action", action)).andExpect(status().isOk())
				.andExpect(model().attributeExists("errorMessage"))
				.andExpect(model().attribute("errorMessage", "No hay suficiente stock disponible para este producto.")); // Verificamos
																															// el
																															// contenido
																															// del
																															// mensaje
																															// de
																															// error
	}
}
