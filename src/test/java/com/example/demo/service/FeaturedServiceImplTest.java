package com.example.demo.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.entity.FeaturedProductEntity;
import com.entity.ProductEntity;
import com.entity.UserEntity;
import com.repository.FeaturedRepository;
import com.service.impl.FeaturedProductServiceImpl;


@ExtendWith(MockitoExtension.class)
public class FeaturedServiceImplTest {

	@InjectMocks
    private FeaturedProductServiceImpl featuredProductService;

    @Mock
    private FeaturedRepository featuredRepository;
    
    @BeforeEach
	public void init() {
		MockitoAnnotations.openMocks(this);
		featuredProductService = new FeaturedProductServiceImpl(featuredRepository);
	}

    @Test
    void getFeaturedById_ValidId_ReturnsFeaturedProduct() throws Exception {
        Long id = 1L;
        FeaturedProductEntity mockedFeatured = new FeaturedProductEntity();
        Mockito.when(featuredRepository.findById(id)).thenReturn(Optional.of(mockedFeatured));

        FeaturedProductEntity result = featuredProductService.getFeaturedById(id);

        Assertions.assertEquals(mockedFeatured, result);
    }

    @Test
    void getFeaturedById_InvalidId_ThrowsException() {
        Long id = 1L;
        Mockito.when(featuredRepository.findById(id)).thenReturn(null);

        Assertions.assertThrows(Exception.class, () -> featuredProductService.getFeaturedById(id));
    }

    @Test
    void findByUser_ValidUser_ReturnsListOfFeaturedProducts() {
        UserEntity user = new UserEntity();
        List<FeaturedProductEntity> mockedFeaturedList = new ArrayList<>();
        Mockito.when(featuredRepository.getByUserId(user.getUserId())).thenReturn(mockedFeaturedList);

        List<FeaturedProductEntity> result = featuredProductService.findByUser(user);

        Assertions.assertEquals(mockedFeaturedList, result);
    }

    @Test
    void addFeatured_ValidProductAndUser_AddsFeaturedProduct() throws Exception {
        ProductEntity product = new ProductEntity();
        UserEntity user = new UserEntity();
        Mockito.when(featuredRepository.save(any(FeaturedProductEntity.class))).thenReturn(new FeaturedProductEntity());

        featuredProductService.addFeatured(product, user);

        verify(featuredRepository, times(1)).save(any(FeaturedProductEntity.class));
    }

    @Test
    void addFeatured_ThrowsException_prodNull() {
        ProductEntity product = null;
        UserEntity user = null;
      
        RuntimeException result = assertThrows(RuntimeException.class,
				() -> featuredProductService.addFeatured(product, user));
		Assertions.assertEquals("Product was null", result.getLocalizedMessage());
	
    }

    @Test
    void addFeatured_AlreadyFeaturedProduct_ThrowsException() {
        ProductEntity product = new ProductEntity();
        UserEntity user = new UserEntity();
       FeaturedProductEntity fav= new FeaturedProductEntity();
       fav.setFeaturedId(1L);
       Mockito.when(featuredRepository.isFav(any(), any())).thenReturn(fav);
     
        RuntimeException result = assertThrows(RuntimeException.class,
				() -> featuredProductService.addFeatured(product, user));
		Assertions.assertEquals("Product was already featured", result.getMessage());
	
    }

    @Test
    void deleteFeaturedProduct_ValidFeaturedProduct_DeletesFeaturedProduct() throws Exception {
        FeaturedProductEntity featuredProduct = new FeaturedProductEntity();

        featuredProductService.deleteFeaturedProduct(featuredProduct);

        verify(featuredRepository, times(1)).delete(featuredProduct);
    }

    @Test
    void isFavourited_ProductIsFavourited_ReturnsTrue() {
        ProductEntity product = new ProductEntity();
        UserEntity user = new UserEntity();
        Mockito.when(featuredRepository.isFav(product.getProductId(), user.getUserId())).thenReturn(new FeaturedProductEntity());

        Boolean result = featuredProductService.isFavourited(product, user.getUserId());

        Assertions.assertTrue(result);
    }

    @Test
    void isFavourited_ProductIsNotFavourited_ReturnsFalse() {
        ProductEntity product = new ProductEntity();
        UserEntity user = new UserEntity();
        Mockito.when(featuredRepository.isFav(product.getProductId(), user.getUserId())).thenReturn(null);

        Boolean result = featuredProductService.isFavourited(product, user.getUserId());

        Assertions.assertFalse(result);
    }
}
