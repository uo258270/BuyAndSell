package com.service;



import com.entity.UserEntity;

public interface UserService {
	
	//findById
	UserEntity findById(Long userId) throws Exception;
	
	//findByNickname
	UserEntity findByUsername(String username) throws Exception;
	
	//findByNickname
	UserEntity findByEmail(String email) throws Exception;
	
	//add
	void addUser(UserEntity user) throws Exception;
	
	//delete
	void deleteUser(Long userId) throws Exception;
	
	//update
	void updateUser(UserEntity user) throws Exception;

	boolean updateMoney(Long userId, Double price);


	void addMoney(String email, Double amount) throws Exception;
}
