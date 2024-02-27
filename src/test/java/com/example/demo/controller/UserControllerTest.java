package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.controller.UserController;
import com.entity.ProductEntity;
import com.entity.UserEntity;
import com.service.ProductService;
import com.service.ShoppingCartService;
import com.service.UserService;
import com.validators.SignUpFormValidator;

import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ProductService productService;

	@Mock
	private UserService userService;
	
	@Mock
	private ShoppingCartService cartService;

	@Mock
	private SignUpFormValidator signUpFormValidator;
	
	@Mock
	private HttpSession httpSession;
	
	@Mock
	private BindingResult bindingresult;

	@InjectMocks
	private UserController userController;

	@Mock
	private Model model;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	String userEmail = "test@example.com";
	Principal principal = new Principal() {
		@Override
		public String getName() {
			return userEmail;
		}
	};

	

	@Test
	public void testSignupPost_ValidUser() throws Exception {
		UserEntity userEntity = new UserEntity();
		userEntity.setEmail("test@example.com");
		userEntity.setPassword("password");
		userEntity.setPasswordConfirm("password");

		doNothing().when(signUpFormValidator).validate(any(), any());
		doNothing().when(userService).addUser(any());
		doNothing().when(userService).autoLogin(any(), any());

		MockHttpServletResponse response = mockMvc.perform(post("/signup").flashAttr("userEntity", userEntity)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/home")).andReturn().getResponse();

		assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND.value());
		assertThat(response.getRedirectedUrl()).isEqualTo("/home");
	
		
	}

	@Test
    public void testHome() throws Exception {
   
        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        ProductEntity product1 = new ProductEntity();
        product1.setProductId(1L);
        product1.setName("Product 1");

        ProductEntity product2 = new ProductEntity();
        product2.setProductId(2L);
        product2.setName("Product 2");

        List<ProductEntity> productList = Arrays.asList(product1, product2);

        when(userService.findByEmail(principal.getName())).thenReturn(user);

        when(productService.getProductsExceptOwn(user.getUserId())).thenReturn(productList);

        MockHttpServletResponse response = mockMvc.perform(get("/home").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("user/home"))
                .andExpect(MockMvcResultMatchers.model().attribute("productList", productList))
                .andExpect(request().sessionAttribute("money", user.getMoney()))
                .andReturn().getResponse();
        
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getForwardedUrl()).isEqualTo("user/home");
    }
	
	@Test
	public void testHomePrincipalNull() throws Exception {
	    MockHttpServletResponse response = mockMvc.perform(get("/home"))
	            .andExpect(status().is3xxRedirection())
	            .andExpect(redirectedUrl("/login"))
	            .andReturn().getResponse();

	    assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND.value());
	    assertThat(response.getRedirectedUrl()).isEqualTo("/login");
	}
	@Test
    public void testList() throws Exception {
        List<UserEntity> usersList = Arrays.asList(new UserEntity(), new UserEntity());

        when(userService.getStandardUsers()).thenReturn(usersList);

        MockHttpServletResponse response = mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("usersList", usersList))
                .andReturn().getResponse();
        
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getForwardedUrl()).isEqualTo("user/list");
    }

    @Test
    public void testDelete() throws Exception {
        UserEntity user1 = new UserEntity();
        user1.setUserId(1L);
        user1.setEmail("user1@example.com");

        UserEntity user2 = new UserEntity();
        user2.setUserId(2L);
        user2.setEmail("user2@example.com");

        Map<String, String> deletedUsers = new HashMap<>();
        deletedUsers.put("user1", "user1@example.com");
        deletedUsers.put("user2", "user2@example.com");

        when(userService.findByEmail("user1@example.com")).thenReturn(user1);
        when(userService.findByEmail("user2@example.com")).thenReturn(user2);

        MockHttpServletResponse response =  mockMvc.perform(post("/user/delete").param("deletedUsers[user1]", "user1@example.com")
                .param("deletedUsers[user2]", "user2@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("usersList", Collections.emptyList()))
                .andReturn().getResponse();
        
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getForwardedUrl()).isEqualTo("user/list");
      
    }
    
    @Test
    public void testProfile() throws Exception {
        String userEmail = "test@example.com";
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return userEmail;
            }
        };

        UserEntity user = new UserEntity();
        user.setUserId(1L);
        user.setEmail(userEmail);

        when(userService.findByEmail(userEmail)).thenReturn(user);

        MockHttpServletResponse response = mockMvc.perform(get("/profile").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user))
                .andReturn().getResponse();
        
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getForwardedUrl()).isEqualTo("user/profile");
    }

    @Test
    public void testAddMoney() throws Exception {
        String userEmail = "test@example.com";
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return userEmail;
            }
        };

        double amount = 100.0;

        MockHttpServletResponse response =  mockMvc.perform(post("/user/addMoney").principal(principal).param("amount", String.valueOf(amount)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andReturn().getResponse();
        
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND.value());
	    assertThat(response.getRedirectedUrl()).isEqualTo("/profile");

       
    }
    
    @Test
    public void testAddMoney_Exception() throws Exception {
        Double amount = 50.0; 
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "test@example.com";
            }
        };
        doThrow(new Exception("Error processing the transaction")).when(userService).addMoney(any(), any());

        MockHttpServletResponse response = mockMvc.perform(post("/user/addMoney")
                .param("amount", String.valueOf(amount))
                .principal(principal))
                .andExpect(status().isOk()) 
                .andExpect(view().name("/error"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getForwardedUrl()).isEqualTo("/error");
    }
    
   
}
