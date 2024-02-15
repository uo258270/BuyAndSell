package com.example.demo.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import com.entity.UserEntity;
import com.service.UserService;
import com.validators.SignUpFormValidator;

public class SignUpFormValidatorTest {

	    private SignUpFormValidator validator;
	    private UserService userServiceMock;
	    private Errors errors;
	    private UserEntity user;

	    @BeforeEach
	    void setUp() {
	    	 userServiceMock = mock(UserService.class);
	    	    validator = new SignUpFormValidator();
	    	    validator.usersService = userServiceMock;
	    	    user = new UserEntity();
	    	    errors = new BeanPropertyBindingResult(user, "user");
	    }

	    @Test
	    void validate_duplicateEmail_shouldReturnError() throws Exception {
	    	 user = new UserEntity(); 
	    	    user.setEmail("existing@example.com");
	    	    user.setName("pepe");
	    	    user.setLastName("lopez"); 
	    	    user.setPassword("password"); 
	    	    user.setPasswordConfirm("password"); 

	    	    when(userServiceMock.findByEmail(any())).thenReturn(new UserEntity());

	    	    validator.validate(user, errors);

	    	    assertTrue(errors.hasFieldErrors("email"));
	    	    verify(userServiceMock, times(1)).findByEmail(any());
	    }

	    @Test
	    void validate_validUser_shouldNotReturnErrors() throws Exception {
	         when(userServiceMock.findByEmail(any())).thenReturn(null);

	        user.setEmail("new@example.com");
	        user.setName("pepe");
	        user.setLastName("lopez");
	        user.setPassword("password");
	        user.setPasswordConfirm("password");

	        validator.validate(user, errors);

	        assertFalse(errors.hasErrors());
	        verify(userServiceMock, times(1)).findByEmail(any());
	    }

	    
	    @Test
	    void validate_nameLengthOutOfRange_shouldReturnError() {
	        UserEntity user = new UserEntity();
	        user.setName("A"); 
	        user.setEmail("new@example.com");
	       
	        user.setLastName("lopez");
	        user.setPassword("password");
	        user.setPasswordConfirm("password");
	        validator.validate(user, errors);

	        assertTrue(errors.hasFieldErrors("name"));
	    }

	    @Test
	    void validate_lastNameLengthOutOfRange_shouldReturnError() {
	        UserEntity user = new UserEntity();
	        user.setEmail("new@example.com");
	        user.setName("pepe");
	        user.setLastName("a");
	        user.setPassword("password");
	        user.setPasswordConfirm("password");
	        validator.validate(user, errors);

	        assertTrue(errors.hasFieldErrors("lastName"));
	    }

	    @Test
	    void validate_passwordLengthOutOfRange_shouldReturnError() {
	        UserEntity user = new UserEntity();
	        user.setEmail("new@example.com");
	        user.setName("pepe");
	        user.setLastName("lopez");
	        user.setPassword("1234");
	        user.setPasswordConfirm("password");
	        validator.validate(user, errors);

	        assertTrue(errors.hasFieldErrors("password"));
	    }

	    @Test
	    void validate_passwordConfirmMismatch_shouldReturnError() {
	        UserEntity user = new UserEntity();
	        user.setEmail("new@example.com");
	        user.setName("pepe");
	        user.setLastName("lopez");
	        user.setPassword("password");
	        user.setPasswordConfirm("differentpassword"); 
	        validator.validate(user, errors);

	        assertTrue(errors.hasFieldErrors("passwordConfirm"));
	    }

	    @Test
	    void validate_invalidEmailFormat_shouldReturnError() {
	        UserEntity user = new UserEntity();
	        user.setName("pepe");
	        user.setLastName("lopez");
	        user.setPassword("password");
	        user.setPasswordConfirm("password");
	        user.setEmail("invalidemail");
	        validator.validate(user, errors);

	        assertTrue(errors.hasFieldErrors("email"));
	    }
	
}
