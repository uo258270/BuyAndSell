package com.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
	
	

	public FeaturedProductServiceImpl(FeaturedRepository featuredRepository) {
		super();
		this.featuredRepository = featuredRepository;
	}

	@Override
	public FeaturedProductEntity getFeaturedById(Long id) throws Exception {
		Optional<FeaturedProductEntity> fav = featuredRepository.findById(id);
		if(fav!=null) {
			return fav.get();
		}
		throw new Exception("Product was null");
	}

	@Override
	public List<FeaturedProductEntity> findByUser(UserEntity user) {
		List<FeaturedProductEntity> list = featuredRepository.getByUserId(user.getUserId());
		return list;
	}

	@Override
	public void addFeatured(ProductEntity product, UserEntity user) throws Exception {
		if(product!=null && user!=null) {
			if(!isFavourited(product, user.getUserId())) {
				FeaturedProductEntity fav = new FeaturedProductEntity();
				fav.setDate(new Date());
				fav.setProduct(product);
				fav.setUser(user);
				featuredRepository.save(fav);
			}else {
				throw new RuntimeException("Product was already featured");
			}
			
		}else {
			throw new RuntimeException("Product was null");
		}
		
	}

	@Override
	public void deleteFeaturedProduct(FeaturedProductEntity featured) throws Exception {
		featuredRepository.delete(featured);
		
	}

	@Override
	public Boolean isFavourited(ProductEntity prod, Long userId) {
		FeaturedProductEntity fav = featuredRepository.isFav(prod.getProductId(), userId);
		if(fav!=null) {
			return true;
		}
		return false;
	}
	
	
	
	

}
