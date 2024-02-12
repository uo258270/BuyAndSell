package com.example.demo.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;

@ExtendWith(MockitoExtension.class)
public class UserEntityTest {

	
	 @Test
	    void testDecMoney() {
	       UserEntity user = new UserEntity();
	        user.setMoney(100.0);
	        user.decMoney(50.0);
	        assertEquals(50.0, user.getMoney(), 0.001);
	    }

	    @Test
	    void testDecMoney_ThrowsIllegalArgumentException() {
	       
	        UserEntity user = new UserEntity();
	        user.setMoney(100.0);

	        assertThrows(IllegalArgumentException.class, () -> {
	            user.decMoney(150.0);
	        });
	    }

	    @Test
	    void testGetCarts_UnmodifiableList() {
	       
	        UserEntity user = new UserEntity();
	        List<ShoppingCartEntity> carts = new ArrayList<>();
	        user.setCarts(carts);

	        List<ShoppingCartEntity> returnedCarts = user.getCarts();

	        assertThrows(UnsupportedOperationException.class, () -> {
	            returnedCarts.add(new ShoppingCartEntity());
	        });
	    }

	    @Test
	    void testEquals() {
	        UserEntity user1 = new UserEntity();
	        user1.setUserId(1L);
	        UserEntity user2 = new UserEntity();
	        user2.setUserId(1L);
	        UserEntity user3 = new UserEntity();
	        user3.setUserId(2L);

	        assertTrue(user1.equals(user2)); 
	        assertFalse(user1.equals(user3));
	        assertFalse(user1.equals(null));
	        
	    }

	    @Test
	    void testHashCode() {
	      
	        UserEntity user1 = new UserEntity();
	        user1.setUserId(1L);
	        UserEntity user2 = new UserEntity();
	        user2.setUserId(1L);
	        UserEntity user3 = new UserEntity();
	        user3.setUserId(2L);

	        assertEquals(user1.hashCode(), user2.hashCode()); 
	        assertNotEquals(user1.hashCode(), user3.hashCode()); 
	    }
}
