package com.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.entity.ProductCartEntity;
import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.exception.NullDataException;
import com.repository.ProductCartRepository;
import com.repository.ProductRepository;
import com.repository.ShoppingCartRepository;
import com.repository.UserRepository;
import com.service.RecommendationSystemService;

@Service
public class RecommendationSystemServiceImpl implements RecommendationSystemService {

	private static final int maxProductos = 5;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ShoppingCartRepository shoRepo;
	
	@Autowired
	private ProductCartRepository productCartRepository;
	
	

	public RecommendationSystemServiceImpl(ProductRepository productRepository, UserRepository userRepository, ShoppingCartRepository shoRepo, ProductCartRepository productCartRepository) {
		super();
		this.productRepository = productRepository;
		this.userRepository = userRepository;
		this.shoRepo  = shoRepo;
		this.productCartRepository = productCartRepository;
	}

	// 5 mas populares del ultimo mes
	@Override
	public List<ProductEntity> getMostPopularProducts() throws NullDataException  {
		Pageable pageable = PageRequest.of(0, 5);
		List<ProductEntity> prods = productRepository.findMostPopularProductsThisMonthLimited(pageable);
		if (prods != null) {
			return prods;
		} else {
			throw new NullDataException("There are no popular products");
		}

	}
	
	//aqui para sacar los "recomendados para ti" basado en reseñas y en compras
	@Override
	public List<ProductEntity> getRecommendedProducts(Long userId){
		List<ProductEntity> byRevs = getProductsBySimilarReviewUsers(userId);
		List<ProductEntity> byShops = getProductsBySimilarUserCarts(userId);
		
		List<ProductEntity> combinedList = new ArrayList<>();
	    combinedList.addAll(byRevs);
	    combinedList.addAll(byShops);
	    
	    List<ProductEntity> distinctRecommendedProducts = combinedList.stream().distinct().collect(Collectors.toList());
	    
	    return distinctRecommendedProducts;
	}

	// ------------------------------------------------------------------------
	// filtrado colaborativo basado en la correlacion de pearson.
	// recomendados para el usuario basados en gustos en comun con otros usuarios
	
	public List<ProductEntity> getProductsBySimilarReviewUsers(Long userId) {
		UserEntity user = userRepository.findByUserId(userId);

		// Calcular similitud entre usuarios usando la correlación de Pearson
		List<UserSimilarity> userSimilarities = new ArrayList<>();
		for (UserEntity otherUser : userRepository.findAll()) {
			if (otherUser.getUserId() != user.getUserId() && !otherUser.getReviews().isEmpty()) {
				double similarity = calculatePearsonCorrelation(user, otherUser);
				userSimilarities.add(new UserSimilarity(otherUser, similarity));
			}
		}

		Set<ProductEntity> recommendedProducts = new HashSet<>();

		userSimilarities.sort(Comparator.comparingDouble(UserSimilarity::getSimilarity).reversed());
		for (UserSimilarity similarity : userSimilarities) {
			for (ReviewEntity review : similarity.getUser().getReviews()) {
				// para que no me recomiende un producto que ya he valorado
				boolean alreadyReviewed = false;
				for (ReviewEntity userReview : user.getReviews()) {
					if (userReview.getProduct().equals(review.getProduct())) {
						alreadyReviewed = true;
						break;
					}
				}
				//que los productos no pertenezcan al propio usuario
				if (!user.equals(review.getProduct().getUser())) {
					if (review.getRating() >= 4) {
						recommendedProducts.add(review.getProduct());
					}

					if (recommendedProducts.size() >= maxProductos) {
						return new ArrayList<>(recommendedProducts);
					}
				}
			}
		}

		return new ArrayList<>(recommendedProducts);
	}

	// Correlacion de Pearson
	public double calculatePearsonCorrelation(UserEntity user1, UserEntity user2) {
		List<ReviewEntity> ratings1 = user1.getReviews();
		List<ReviewEntity> ratings2 = user2.getReviews();

		// Calcular la media de las calificaciones de cada usuario
		double mean1 = calculateMean(ratings1);
		double mean2 = calculateMean(ratings2);

		// Calcular la suma de los productos de las diferencias entre las calificaciones
		// y las medias
		double numerator = 0.0;
		double denominator1 = 0.0;
		double denominator2 = 0.0;

		for (ReviewEntity rating1 : ratings1) {
			for (ReviewEntity rating2 : ratings2) {
				// Compara si las calificaciones pertenecen al mismo producto, solo queremos
				// comparar calificaciones para el mismo producto.
				if (rating1.getProduct().equals(rating2.getProduct())) {
					// Estas diferencias indican cuánto se desvían las calificaciones individuales
					// de sus respectivas medias.
					double diff1 = rating1.getRating() - mean1;
					double diff2 = rating2.getRating() - mean2;

					// El numerador de la fórmula de correlación de Pearson es la suma acumulativa
					// del producto de las diferencias (diff1 y diff2) para cada par de
					// calificaciones
					numerator += diff1 * diff2;
					// Los denominadores de la fórmula de correlación de Pearson son las sumas
					// acumulativas de los cuadrados de las diferencias (diff1 y diff2)
					denominator1 += Math.pow(diff1, 2);
					denominator2 += Math.pow(diff2, 2);
				}
			}
		}

		if (denominator1 == 0 || denominator2 == 0) {
			return 0.0;
		}

		// Calcular la correlación de Pearson
		return numerator / (Math.sqrt(denominator1) * Math.sqrt(denominator2));
	}

	// media
	private double calculateMean(List<ReviewEntity> ratings) {
		if (ratings.isEmpty()) {
			return 0.0;
		}

		double sum = ratings.stream().mapToDouble(ReviewEntity::getRating).sum();
		return sum / ratings.size();
	}

	private static class UserSimilarity {
		private final UserEntity user;
		private final double similarity;

		public UserSimilarity(UserEntity user, double similarity) {
			this.user = user;
			this.similarity = similarity;
		}

		public UserEntity getUser() {
			return user;
		}

		public double getSimilarity() {
			return similarity;
		}
	}

//-----------------------------------------------------------------------------	
	// mejores valorados
	@Override
	public List<ProductEntity> getTopRatedProducts() throws NullDataException {
		List<ProductEntity> prods = productRepository.findAllByOrderByAverageRatingDesc();
		if (prods != null) {
			return prods;
		} else {
			throw new NullDataException("there are the best rated products");
		}

	}

//--------------------------------------------------------------------------------
	// filtrado colaborativo basados en las compras de otros usuarios
	public List<ProductEntity> getProductsBySimilarUserCarts(Long userId) {
	    List<ShoppingCartEntity> userPurchases = userRepository.getShoppingCartsByUserId(userId);
	    List<UserEntity> similarUsers = userRepository.findAll();

	    // Calcular porcentaje de compras similares
	    //TODO pruebas
	    double similarityThreshold = 0.3;

	    List<UserEntity> similarUsersList = new ArrayList<>();
	    for (UserEntity user : similarUsers) {
	        if (!user.getUserId().equals(userId)) {
	            double similarity = calculateSimilarity(userPurchases, user.getCarts());
	            if (similarity >= similarityThreshold) {
	                similarUsersList.add(user);
	            }
	        }
	    }

	    // Filtrar las compras de los usuarios similares
	    List<Long> similarUserPurchases = new ArrayList<>();
	    for (UserEntity user : similarUsersList) {
	        for (ShoppingCartEntity cart : user.getCarts()) {
	            for (ProductCartEntity product : cart.getProductCartEntities()) {
	                similarUserPurchases.add(product.getProduct().getProductId());
	            }
	        }
	    }

	    // Filtrar productos que el usuario ya haya comprado
	    Set<ProductEntity> recommendedProductsSet = new HashSet<>();
	    for (Long productId : similarUserPurchases) {
	        boolean alreadyPurchased = isProductAlreadyPurchased(userId, userPurchases, productId);

	        // mirar que no pertenezca al usuario
	        ProductEntity product = productRepository.findById(productId).orElse(null);
	        if (!alreadyPurchased && product != null && !product.getUser().getUserId().equals(userId)) {
	        	recommendedProductsSet.add(product);
	        }
	    }
	    

	    return new ArrayList<>(recommendedProductsSet);
	}

	private boolean isProductAlreadyPurchased(Long userId, List<ShoppingCartEntity> userPurchases, Long productId) {
		if(shoRepo.countByUserIdAndProductId(userId, productId) > 0) {
			return true;
		}
		return false;

	}

	private double calculateSimilarity(List<ShoppingCartEntity> userPurchases,
	        List<ShoppingCartEntity> otherUserPurchases) {
	    List<Long> userProductIds = getProductIds(userPurchases);
	    List<Long> otherUserProductIds = getProductIds(otherUserPurchases);

	    // se calculan los productos que han comprados ambos usuarios
	    List<Long> commonProductIds = new ArrayList<>();
	    for (Long productId : userProductIds) {
	        if (otherUserProductIds.contains(productId)) {
	            commonProductIds.add(productId);
	        }
	    }

	    // Calcular el porcentaje de compras similares
	    return (double) commonProductIds.size() / userProductIds.size();
	}

	private List<Long> getProductIds(List<ShoppingCartEntity> purchases) {
		return productCartRepository.findProductIdsByShoppingCarts(purchases);
	}

}
