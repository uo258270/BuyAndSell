package com.entity;


import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.entity.enums.RoleEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="USER")
public class UserEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long userId;
	
	private String name;
	
	private String lastName;
	
	@Column(unique = true)
	private String email;
	
	@Column(unique = true)
	private String username;
	
	private String password;
	
	private String passwordConfirm;
	
	private String address;
	
	@Enumerated(EnumType.STRING)
	private RoleEnum role;
	
	private Date registerDate;
	
	private Double money;
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<ShoppingCartEntity> carts;
	

	 @OneToMany(mappedBy = "userEntity")
	 private List<ReviewEntity> reviews;
	 
	public List<ReviewEntity> getReviews() {
		return reviews;
	}

	public void setReviews(List<ReviewEntity> reviews) {
		this.reviews = reviews;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setCarts(List<ShoppingCartEntity> carts) {
		this.carts = carts;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}
	
	public void decMoney(double qty) {
		if (this.money < qty) throw new IllegalArgumentException();
		this.money -= qty;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public List<ShoppingCartEntity> getCarts() {
		return Collections.unmodifiableList(carts);
	}
	
	/* package private */ List<ShoppingCartEntity> _getCarts() {
		return this.carts;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public RoleEnum getRole() {
		return role;
	}
	
	public String getRoleName() {
		return role.name();
	}

	public void setRole(RoleEnum roleUser) {
		this.role = roleUser;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserEntity other = (UserEntity) obj;
		return  Objects.equals(userId, other.userId);
	}

	
	
	
	
}
