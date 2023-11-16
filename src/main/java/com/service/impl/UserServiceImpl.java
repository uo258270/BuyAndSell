package com.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.entity.UserEntity;
import com.repository.UserRepository;
import com.service.UserService;


@Service("UserServiceImpl")
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	public UserServiceImpl() {
		super();
		
	}
	
	String[] roles = { "ROLE_USER", "ROLE_ADMIN" };

	public String[] getRoles() {
		return roles;
	}
	
	
	public String findLoggedInEmail() {
		Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
		if (userDetails instanceof UserDetails) {
			return ((UserDetails) userDetails).getUsername();
		}
		return null;
	}

	public void autoLogin(String email, String password) {
		UserEntity user = userRepository.findByEmail(email);

		if(user == null){
	        throw new UsernameNotFoundException("User not authorized.");
	    }
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole()));

		UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				grantedAuthorities);

		UsernamePasswordAuthenticationToken aToken = new UsernamePasswordAuthenticationToken(userDetails, password,
				userDetails.getAuthorities());
		authenticationManager.authenticate(aToken);
		if (aToken.isAuthenticated()) {
			SecurityContextHolder.getContext().setAuthentication(aToken);
			logger.debug(String.format("Auto login %s successfully!", email));
		}
	}

	@Override
	public UserEntity findById(Long userId) throws Exception {
		if (userId != null) {
			UserEntity response = userRepository.findByUserId(userId);
			if (response == null) {
				throw new Exception("Query does not found results");
			}
			return response;
		} else {
			throw new Exception("userId con not be null");
		}
	}

	@Override
	public UserEntity findByUsername(String username) throws Exception {
		if (username != null) {
			UserEntity response = userRepository.findByUsername(username);
			if (response == null) {
				throw new Exception("Query does not found results");
			}
			return response;
		} else {
			throw new Exception("username con not be null");
		}
	}

	@Override
	public void addUser(UserEntity user) throws Exception {
		validateUser(user);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);

	}

	private void validateUser(UserEntity user) throws Exception {
		if (user != null) {
			if (user.getUserId() != null) {
				UserEntity response = userRepository.findByUserId(user.getUserId());
				if (response != null) {
					throw new Exception("This user already exists in the data base");
				}
				throw new Exception("userId can not be null");
			}
			throw new Exception("user can not be null");
		}

	}

	@Override
	public void deleteUser(Long userId) throws Exception {
		if (userId != null) {
			userRepository.deleteUser(userId);
		} else {
			throw new Exception("userId can not be null");
		}
	}

	@Override
	public void updateUser(UserEntity user) {
		userRepository.save(user);
	}

	@Override
	public UserEntity findByEmail(String email) throws Exception {
		if (email != null) {
			UserEntity response = userRepository.findByEmail(email);
			if (response == null) {
				throw new Exception("Query does not found results");
			}
			return response;
		} else {
			throw new Exception("email can not be null");
		}
	}
	
	public List<UserEntity> getStandardUsers(){
		List<UserEntity> users = new ArrayList<UserEntity>();
		users = userRepository.findByRole("ROLE_USER");
		return users;
	}
	
	public boolean updateMoney(Long id, Double money) {
		UserEntity user = userRepository.findById(id).get();
		if (money > user.getMoney()) {
			return false;
		}
		user.setMoney(user.getMoney() - money);
		userRepository.save(user);
		return true;
	}


	
}
