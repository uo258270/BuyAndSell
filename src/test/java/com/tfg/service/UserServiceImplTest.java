package com.tfg.service;
import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tfg.entity.UserEntity;
import com.tfg.entity.enums.RoleEnum;
import com.tfg.exception.NotFoundException;
import com.tfg.exception.NullDataException;
import com.tfg.repository.FeaturedRepository;
import com.tfg.repository.ProductCartRepository;
import com.tfg.repository.ProductRepository;
import com.tfg.repository.ReviewRepository;
import com.tfg.repository.UserRepository;
import com.tfg.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ReviewRepository reviewRepo;

	@Mock
	private ProductCartRepository productCartRepository;

	@Mock
	private FeaturedRepository favRepo;

	@Mock
	private UserRepository userRepo;

	@Mock
	private PasswordEncoder passEncoder;

	@Mock
	private AuthenticationManager authenticationManager;

	@BeforeEach
	public void init() {
		MockitoAnnotations.openMocks(this);
		userService = new UserServiceImpl(userRepo, passEncoder, authenticationManager);
	}

	@Test
	void findById_UserExists_ReturnsUserEntity() throws Exception {

		Long userId = 1L;
		UserEntity mockedUser = createMockedUser();
		Mockito.when(userRepo.findByUserId(any())).thenReturn(mockedUser);

		UserEntity result = userService.findById(userId);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(userId, result.getUserId());

	}

	

	@Test
	void findById_UserNotExists_ThrowsException() {

		Mockito.when(userRepo.findByUserId(any())).thenReturn(null);
		Assertions.assertThrows(Exception.class, () -> userService.findById(1L));
	}

	


    @Test
    void autoLogin_ValidCredentials_AuthenticatesAndLogsIn() {
        String email = "1";
        String password = "1";
        UserEntity user = createMockedUser();
        Authentication authenticatedAuthentication = createMockedAuthentication();
        Mockito.when(userRepo.findByEmail(any())).thenReturn(user);
        Mockito.when(authenticationManager.authenticate(any())).thenReturn(authenticatedAuthentication);
        userService.autoLogin(email, password);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertNotNull(authentication);
        Set<GrantedAuthority> expectedAuthorities = new HashSet<>(Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        Set<GrantedAuthority> actualAuthorities = new HashSet<>(authentication.getAuthorities());
        Assertions.assertEquals(expectedAuthorities, actualAuthorities);
    }

    @Test
    void autoLogin_InvalidCredentials_ThrowsUsernameNotFoundException() {
        String email = "mockedUser";
        String password = "invalidPassword";
        Mockito.when(userRepo.findByEmail(any())).thenReturn(null);
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.autoLogin(email, password));
    }
    
    
    @Test
    void findByUsername_ValidUsername_ReturnsUserEntity() throws Exception {
        String username = "1";
        UserEntity mockedUser = createMockedUser();
        Mockito.when(userRepo.findByUsername(any())).thenReturn(mockedUser);

        UserEntity result = userService.findByUsername(username);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("1", result.getUsername());
    }

    @Test
    void findByUsername_NullUsername_ThrowsException() {
        Assertions.assertThrows(Exception.class, () -> userService.findByUsername(null));
    }

    @Test
    void addUser_ValidUser_SavesUserEntity() throws Exception {
        UserEntity user = createMockedUser();
        Mockito.when(passEncoder.encode(any())).thenReturn("encodedPassword");

        userService.addUser(user);

        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }

    @Test
    void deleteUser_ValidUserId_DeletesUserEntity() throws Exception {
        Long userId = 1L;

        userService.deleteUser(userId);

        Mockito.verify(userRepo, Mockito.times(1)).deleteUser(userId);
    }

    @Test
    void deleteUser_NullUserId_ThrowsException() {
        Assertions.assertThrows(Exception.class, () -> userService.deleteUser(null));
    }

    @Test
    void updateUser_ValidUser_SavesUserEntity() {
        UserEntity user = createMockedUser();

        userService.updateUser(user);

        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }
    @Test
    void findByEmail_ValidEmail_ReturnsUserEntity() throws NullDataException {
       
        UserEntity mockedUser = createMockedUser();
        Mockito.when(userRepo.findByEmail(any())).thenReturn(mockedUser);

        UserEntity result = userService.findByEmail("1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("1", result.getEmail());
    }
    
    @Test
    void findByEmail_EmailNotFoundException_ThrowsNotFoundException() {
        Mockito.when(userRepo.findByEmail(any())).thenThrow(new NotFoundException("Test exception"));

        Assertions.assertThrows(NotFoundException.class, () -> userService.findByEmail("1"));
    }

    @Test
    void getStandardUsers_ReturnsListOfUserEntities() {
        List<UserEntity> mockedUsers = Collections.singletonList(createMockedUser());
        Mockito.when(userRepo.findByRole(RoleEnum.ROLE_USER)).thenReturn(mockedUsers);

        List<UserEntity> result = userService.getStandardUsers();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mockedUsers, result);
    }

    @Test
    void updateMoney_ValidIdAndMoney_ReturnsTrueAndSavesUserEntity() {
        Long userId = 1L;
        Double money = 5.0;
        UserEntity mockedUser = createMockedUser();
        mockedUser.setMoney(10.0);
        Mockito.when(userRepo.findById(userId)).thenReturn(Optional.of(mockedUser));

        boolean result = userService.updateMoney(userId, money);

        Assertions.assertTrue(result);
        Assertions.assertEquals(5.0, mockedUser.getMoney());
        Mockito.verify(userRepo, Mockito.times(1)).save(mockedUser);
    }

    @Test
    void updateMoney_InvalidMoney_ReturnsFalseAndDoesNotSaveUserEntity() {
        Long userId = 1L;
        Double money = 15.0;
        UserEntity mockedUser = createMockedUser();
        mockedUser.setMoney(10.0);
        Mockito.when(userRepo.findById(userId)).thenReturn(Optional.of(mockedUser));

        boolean result = userService.updateMoney(userId, money);

        Assertions.assertFalse(result);
        Assertions.assertEquals(10.0, mockedUser.getMoney());
        Mockito.verify(userRepo, Mockito.never()).save(mockedUser);
    }

    @Test
    void addMoney_ValidEmailAndAmount_SavesUserEntityWithIncreasedMoney() throws Exception {
        String email = "test@example.com";
        Double amount = 5.0;
        UserEntity mockedUser = createMockedUser();
        Mockito.when(userRepo.findByEmail(email)).thenReturn(mockedUser);

        userService.addMoney(email, amount);

        Assertions.assertEquals(15.0, mockedUser.getMoney());
        Mockito.verify(userRepo, Mockito.times(1)).save(mockedUser);
    }

    @Test
    void addMoney_InvalidAmount_ThrowsException() {
        String email = "test@example.com";
        Double amount = -5.0;
        Assertions.assertThrows(Exception.class, () -> userService.addMoney(email, amount));
    }
   
    @Test
    void addMoney_ValidEmailAndAmount_SavesUserEntityWithIncreasedMoney2() throws Exception {
        String email = "test@example.com";
        Double amount = 5.0;
        UserEntity mockedUser = createMockedUser();
        Mockito.when(userRepo.findByEmail(email)).thenReturn(mockedUser);
        Mockito.when(userRepo.save(Mockito.any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity savedUser = invocation.getArgument(0);
            savedUser.setMoney(savedUser.getMoney() + amount);
            return savedUser;
        });

        userService.addMoney(email, amount);

        Assertions.assertEquals(20.0, mockedUser.getMoney());
        Mockito.verify(userRepo, Mockito.times(1)).save(Mockito.any(UserEntity.class));
    }

    @Test
    void addMoney_ValidEmailAndAmount_SavesUserEntityWithIncreasedMoney3() throws Exception {
        Double amount = 5.0;
        UserEntity mockedUser = createMockedUser();
        Mockito.when(userRepo.findByEmail(any())).thenReturn(mockedUser);
        mockedUser.setMoney(null);
        userService.addMoney(any(), amount);

        Assertions.assertEquals(5.0, mockedUser.getMoney());
        Mockito.verify(userRepo, Mockito.times(1)).save(Mockito.any(UserEntity.class));
    }

    
	private UserEntity createMockedUser() {
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
		return user;
	}
	
	private Authentication createMockedAuthentication() {
        return new UsernamePasswordAuthenticationToken("1", "1", new HashSet<>());
    }
	
	
}
