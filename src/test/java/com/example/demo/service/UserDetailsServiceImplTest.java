package com.example.demo.service;

import com.entity.UserEntity;
import com.entity.enums.RoleEnum;
import com.repository.UserRepository;
import com.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

	 @InjectMocks
	    private UserDetailsServiceImpl userDetailsService;

	    @Mock
	    private UserRepository userRepository;

	    @BeforeEach
	    void setUp() {
	        userDetailsService = new UserDetailsServiceImpl(userRepository);
	    }

	    @Test
	    void loadUserByUsername_UserExists() {
	      
	        String userEmail = "email";
	        UserEntity mockUser = new UserEntity();
	        mockUser.setEmail(userEmail);
	        mockUser.setPassword("pass");  
	        mockUser.setRole(RoleEnum.ROLE_ADMIN);

	        Mockito.when(userRepository.findByEmail(any())).thenReturn(mockUser);

	        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

	        Assertions.assertEquals(userEmail, userDetails.getUsername());
	        Assertions.assertEquals("pass", userDetails.getPassword());  
	        Assertions.assertTrue(userDetails.getAuthorities().stream()
	                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
	    }

	    @Test
	    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
	         String emailNotExist = "emailN";
	        Mockito.when(userRepository.findByEmail(any())).thenReturn(null);

	        Assertions.assertThrows(UsernameNotFoundException.class, () ->   userDetailsService.loadUserByUsername(emailNotExist));
	    }
	}
