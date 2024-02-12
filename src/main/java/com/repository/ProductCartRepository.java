package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.entity.ProductCartEntity;
import com.entity.ProductEntity;
import com.entity.ShoppingCartEntity;

@Repository
public interface ProductCartRepository extends JpaRepository<ProductCartEntity, Long>{

	
	@Query("SELECT pce FROM ProductCartEntity pce WHERE pce.cart.id = :cartId")
	List<ProductCartEntity> findProductCartsByCartId(@Param("cartId") Long cartId);
	
	@Query("SELECT pc.product FROM ProductCartEntity pc WHERE pc.cart.user.id = :userId")
	List<ProductEntity> getPurchasedProductsByUserId(@Param("userId") Long userId);

	 @Query("SELECT pce.product.productId FROM ProductCartEntity pce WHERE pce.cart IN :shoppingCarts")
	 List<Long> findProductIdsByShoppingCarts(@Param("shoppingCarts") List<ShoppingCartEntity> shoppingCarts);

	
}
