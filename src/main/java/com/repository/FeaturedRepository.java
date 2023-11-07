package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.entity.FeaturedProductEntity;

public interface FeaturedRepository extends JpaRepository<FeaturedProductEntity, Long>{
	
	//pregunta como relacionar product con fav
	@Query("Select f from FeaturedProductEntity f where f.product.id = :productId")
	//@Query("Select * from FeaturedProductEntity f inner join ProductEntity p on f.productId = p.productId where p.productId= :productId")
	FeaturedProductEntity getByProductId(Long productId);
	
	//pregunta como relacionar product con fav
	@Query("Select f from FeaturedProductEntity f  where f.user.userId= :userId")
	List<FeaturedProductEntity> getByUserId(Long userId);

}
