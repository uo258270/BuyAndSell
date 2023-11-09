package com.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.FeaturedProductEntity;
import com.entity.ProductEntity;
import com.entity.UserEntity;
import com.repository.FeaturedRepository;
import com.service.FeaturedProductService;

@Service
public class FeaturedProductServiceImpl implements FeaturedProductService{
	
	@Autowired
	private FeaturedRepository featuredRepository;

	@Override
	public Boolean isFeatured(ProductEntity product) {
		FeaturedProductEntity fav = featuredRepository.getByProductId(product.getProductId());
		if(fav!=null) {
			return true;
		}
		return false;
	}

	@Override
	public List<FeaturedProductEntity> findByUser(UserEntity user) throws Exception {
		List<FeaturedProductEntity> list = featuredRepository.getByUserId(user.getUserId());
		if(list!=null) {
			return list;
		}else {
			throw new Exception("This user does not have any featured products");
		}
	}

	@Override
	public void addFeatured(ProductEntity product) throws Exception {
		if(product!=null && product.getUser()!=null) {
			FeaturedProductEntity fav = new FeaturedProductEntity();
			fav.setDate(new Date());
			fav.setProduct(product);
			fav.setUser(product.getUser());
		}else {
			throw new Exception("Product was null");
		}
		
	}

	@Override
	public void deleteFeaturedProduct(FeaturedProductEntity featured) throws Exception {
		featuredRepository.delete(featured);
		
	}
	
	
	
	

}
