package com.example.demo.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.entity.ProductCartEntity;
import com.entity.ProductEntity;
import com.exception.InvalidStockException;

@ExtendWith(MockitoExtension.class)
public class ProductCartEntityTest {
	
	@Test
    void incQuantity_IncreaseQuantity_ThrowsInvalidStockException() {
        ProductCartEntity productCart = new ProductCartEntity();
        productCart.setQuantityInCart(5);
        assertThrows(InvalidStockException.class, () -> productCart.incQuantity(mockProductEntityWithStock(5)));
    }

    @Test
    void equals_SameObject_ReturnsTrue() {
        ProductCartEntity productCart = new ProductCartEntity();
        assertTrue(productCart.equals(productCart));
    }

    @Test
    void equals_NullObject_ReturnsFalse() {
        ProductCartEntity productCart = new ProductCartEntity();
        assertFalse(productCart.equals(null));
    }

    @Test
    void equals_DifferentClassObject_ReturnsFalse() {
        ProductCartEntity productCart = new ProductCartEntity();
        assertFalse(productCart.equals(new Object()));
    }

    @Test
    void equals_SameId_ReturnsTrue() {
        ProductCartEntity productCart1 = new ProductCartEntity();
        productCart1.setId(1L);
        ProductCartEntity productCart2 = new ProductCartEntity();
        productCart2.setId(1L);
        assertTrue(productCart1.equals(productCart2));
    }

    private ProductEntity mockProductEntityWithStock(int stock) {
        ProductEntity productEntity = mock(ProductEntity.class);
        when(productEntity.getStock()).thenReturn(stock);
        return productEntity;
    }
    
    @Test
    void hashCode_SameId_ReturnsSameHashCode() {
        ProductCartEntity productCart1 = new ProductCartEntity();
        productCart1.setId(1L);
        ProductCartEntity productCart2 = new ProductCartEntity();
        productCart2.setId(1L);
        assertEquals(productCart1.hashCode(), productCart2.hashCode());
    }

    @Test
    void hashCode_DifferentId_ReturnsDifferentHashCode() {
        ProductCartEntity productCart1 = new ProductCartEntity();
        productCart1.setId(1L);
        ProductCartEntity productCart2 = new ProductCartEntity();
        productCart2.setId(2L);
        assertNotEquals(productCart1.hashCode(), productCart2.hashCode());
    }

    @Test
    void getId_ReturnsCorrectId() {
        Long id = 1L;
        ProductCartEntity productCart = new ProductCartEntity();
        productCart.setId(id);
        assertEquals(id, productCart.getId());
    }

}
