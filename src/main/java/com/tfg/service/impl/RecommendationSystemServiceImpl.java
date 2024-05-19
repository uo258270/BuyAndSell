package com.tfg.service.impl;

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

import com.tfg.entity.ProductCartEntity;
import com.tfg.entity.ProductEntity;
import com.tfg.entity.ReviewEntity;
import com.tfg.entity.ShoppingCartEntity;
import com.tfg.entity.SimilarProductEntity;
import com.tfg.entity.SimilarUserEntity;
import com.tfg.entity.UserEntity;
import com.tfg.exception.NullDataException;
import com.tfg.repository.ProductCartRepository;
import com.tfg.repository.ProductRepository;
import com.tfg.repository.ShoppingCartRepository;
import com.tfg.repository.SimilarProductRepository;
import com.tfg.repository.SimilarUserRepository;
import com.tfg.repository.UserRepository;
import com.tfg.service.RecommendationSystemService;

import jakarta.transaction.Transactional;

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

	@Autowired
	private SimilarProductRepository similarProductRepository;

	@Autowired
	private SimilarUserRepository similarUserRepository;

	public RecommendationSystemServiceImpl(ProductRepository productRepository, UserRepository userRepository,
			ShoppingCartRepository shoRepo, ProductCartRepository productCartRepository,
			SimilarProductRepository similarProductRepository, SimilarUserRepository similarUserRepository) {
		super();
		this.productRepository = productRepository;
		this.userRepository = userRepository;
		this.shoRepo = shoRepo;
		this.productCartRepository = productCartRepository;
		this.similarUserRepository = similarUserRepository;
		this.similarProductRepository = similarProductRepository;
	}

	// 5 mas populares del ultimo mes
	@Override
	public List<ProductEntity> getMostPopularProducts() throws NullDataException {
		Pageable pageable = PageRequest.of(0, 5);
		List<ProductEntity> prods = productRepository.findMostPopularProductsThisMonthLimited(pageable);
		if (prods == null || prods.isEmpty()) {
	        throw new NullDataException("There are no popular products");
	    }
		List<ProductEntity> popularProducts = new ArrayList<>();
		for (ProductEntity product : prods) {
			Integer numOfPurchases = productCartRepository.countProductOrdersThisMonth(product.getProductId());
			if (numOfPurchases > 0) {
				product.setNumOfPurchases(numOfPurchases);
				popularProducts.add(product);
			}
		}
		popularProducts.sort(Comparator.comparingInt(ProductEntity::getNumOfPurchases).reversed());

		return prods;
		

	}

	// aqui para sacar los "recomendados para ti" basado en reseñas y en compras
	@Override
	public List<ProductEntity> getRecommendedProducts(Long userId) throws NullDataException {
		if (userId == null) {
			throw new NullDataException("userId is null");
		}
		UserEntity user = userRepository.findByUserId(userId);
		List<ProductEntity> byRevs = getProductsBySimilarReviewUsers(userId);
		List<ProductEntity> byShops = getProductsBySimilarUserCarts(userId);

		List<ProductEntity> combinedList = new ArrayList<>();
		combinedList.addAll(byRevs);
		combinedList.addAll(byShops);

		List<ProductEntity> distinctRecommendedProducts = combinedList.stream()
				.filter(product -> !product.perteneceAUsuario(user.getEmail())).distinct().collect(Collectors.toList());
		return distinctRecommendedProducts;

	}

	// ------------------------------------------------------------------------
	// filtrado colaborativo basado en la correlacion de pearson.
	// recomendados para el usuario basados en gustos en comun con otros usuarios

	@Transactional
	public List<ProductEntity> getProductsBySimilarReviewUsers(Long userId) {
		UserEntity user = userRepository.findByUserId(userId);
		List<ShoppingCartEntity> userPurchases = userRepository.getShoppingCartsByUserId(userId);

		// Obtener la lista de usuarios similares basada en alguna métrica de similitud,
		// como correlación de Pearson
		List<SimilarUserEntity> similarUsers = similarUserRepository.getSimilarUsersByUserId(userId);

		Set<ProductEntity> recommendedProducts = new HashSet<>();

		for (SimilarUserEntity similarUser : similarUsers) {
			UserEntity otherUser = similarUser.getUser1().equals(user) ? similarUser.getUser2()
					: similarUser.getUser1();

			for (ReviewEntity review : otherUser.getReviews()) {
				// Para evitar recomendar productos que ya ha comprado el usuario actual
				if (!isProductAlreadyPurchased(userId, userPurchases, review.getProduct().getProductId())) {
					boolean alreadyReviewed = user.getReviews().stream()
							.anyMatch(userReview -> userReview.getProduct().equals(review.getProduct()));
					if (!alreadyReviewed && review.getRating() >= 4 && !user.equals(review.getProduct().getUser())) {
						recommendedProducts.add(review.getProduct());
						if (recommendedProducts.size() >= maxProductos) {
							return new ArrayList<>(recommendedProducts);
						}
					}
				}
			}
		}

		return new ArrayList<>(recommendedProducts);
	}

	// Correlacion de Pearson
	public double calculatePearsonCorrelation(UserEntity user1, UserEntity user2) {
		if (user1.getReviews() == null || user2.getReviews() == null) {
			return 0.0; // O cualquier valor predeterminado que desees cuando las revisiones son null
		}

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

	@Transactional
	@Override
	public void executeDailySimilarityCalculationReviews() {
		// Obtener todos los usuarios
		List<UserEntity> users = userRepository.findAll();

		// Iterar sobre cada par de usuarios
		for (int i = 0; i < users.size(); i++) {
			UserEntity user1 = users.get(i);
			for (int j = i + 1; j < users.size(); j++) {
				UserEntity user2 = users.get(j);

				// Calcular la similitud entre los usuarios
				double similarity = calculatePearsonCorrelation(user1, user2);

				// Guardar la similitud en la base de datos
				SimilarUserEntity similarUserEntity = new SimilarUserEntity();
				similarUserEntity.setUser1(user1);
				similarUserEntity.setUser2(user2);
				similarUserEntity.setSimilarity(similarity);
				similarUserRepository.save(similarUserEntity);
			}
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
			throw new NullDataException("there are not best rated products");
		}

	}

//--------------------------------------------------------------------------------
	// filtrado colaborativo basados en las compras de otros usuarios
	@Transactional
	public List<ProductEntity> getProductsBySimilarUserCarts(Long userId) {
		// Obtener la lista de compras del usuario actual
		List<ShoppingCartEntity> userPurchases = userRepository.getShoppingCartsByUserId(userId);

		// Obtener las parejas de usuarios similares desde la base de datos
		List<SimilarUserEntity> similarUsers = similarUserRepository.getSimilarUsersByUserId(userId);

		// Filtrar las parejas similares que tengan una similitud por encima de cierto
		// umbral
		double similarityThreshold = 0.3;
		List<SimilarUserEntity> similarUsersAboveThreshold = new ArrayList<>();
		for (SimilarUserEntity similarUser : similarUsers) {
			if (similarUser.getSimilarity() >= similarityThreshold) {
				similarUsersAboveThreshold.add(similarUser);
			}
		}

		// Obtener las compras de los usuarios similares
		List<Long> similarUserPurchases = new ArrayList<>();
		for (SimilarUserEntity similarUser : similarUsersAboveThreshold) {
			// Obtener el ID del usuario similar
			Long similarUserId = similarUser.getUser2().getUserId();
			List<ShoppingCartEntity> similarUserCarts = userRepository.getShoppingCartsByUserId(similarUserId);
			for (ShoppingCartEntity cart : similarUserCarts) {
				for (ProductCartEntity productCart : cart.getProductCartEntities()) {
					similarUserPurchases.add(productCart.getProduct().getProductId());
				}
			}
		}

		// Filtrar productos que el usuario ya haya comprado
		Set<Long> alreadyPurchasedProductIds = new HashSet<>();
		for (ShoppingCartEntity purchase : userPurchases) {
			for (ProductCartEntity productCart : purchase.getProductCartEntities()) {
				alreadyPurchasedProductIds.add(productCart.getProduct().getProductId());
			}
		}

		// Obtener los productos recomendados que no ha comprado el usuario
		List<ProductEntity> recommendedProducts = new ArrayList<>();
		for (Long productId : similarUserPurchases) {
			if (!alreadyPurchasedProductIds.contains(productId)) {
				ProductEntity product = productRepository.findByProductId(productId);
				if (product != null) {
					recommendedProducts.add(product);
				}
			}
		}

		return recommendedProducts;
	}

	public boolean isProductAlreadyPurchased(Long userId, List<ShoppingCartEntity> userPurchases, Long productId) {
		if (shoRepo.countByUserIdAndProductId(userId, productId) > 0) {
			return true;
		}
		return false;

	}

	@Transactional
	@Override
	public void executeDailySimilarityCalculation() {
		// Obtener todas las parejas de usuarios con su similitud
		List<SimilarUserEntity> similarUsers = similarUserRepository.findAll();

		// Iterar sobre cada pareja de usuarios
		for (SimilarUserEntity similarUser : similarUsers) {
			Long userId1 = similarUser.getUser1().getUserId();
			Long userId2 = similarUser.getUser2().getUserId();

			// Obtener las compras de ambos usuarios
			List<ShoppingCartEntity> purchasesUser1 = userRepository.getShoppingCartsByUserId(userId1);
			List<ShoppingCartEntity> purchasesUser2 = userRepository.getShoppingCartsByUserId(userId2);

			// Calcular la similitud entre los usuarios
			double similarity = calculateSimilarity(purchasesUser1, purchasesUser2);

			// Obtener los productos más afines entre los usuarios
			List<ProductEntity> recommendedProducts = getProductsBySimilarUserCarts(userId1);

			// Guardar la similitud y los productos recomendados en la base de datos
			saveSimilarityAndRecommendations(userId1, userId2, similarity, recommendedProducts);
		}
	}

	public void saveSimilarityAndRecommendations(Long userId1, Long userId2, double similarity,
			List<ProductEntity> recommendedProducts) {
		SimilarUserEntity similarityEntity = new SimilarUserEntity();
		similarityEntity.setUser1(userRepository.findById(userId1).orElse(null));
		similarityEntity.setUser2(userRepository.findById(userId2).orElse(null));
		similarityEntity.setSimilarity(similarity);

		similarUserRepository.save(similarityEntity);

		for (int i = 0; i < recommendedProducts.size(); i++) {
			ProductEntity product1 = recommendedProducts.get(i);
			for (int j = i + 1; j < recommendedProducts.size(); j++) {
				ProductEntity product2 = recommendedProducts.get(j);

				SimilarProductEntity similarProductEntity = new SimilarProductEntity();
				similarProductEntity.setProd1(product1);
				similarProductEntity.setProd2(product2);

				similarProductRepository.save(similarProductEntity);
			}
		}
	}

	public double calculateSimilarity(List<ShoppingCartEntity> userPurchases,
			List<ShoppingCartEntity> otherUserPurchases) {
		List<Long> userProductIds = getProductIds(userPurchases);
		List<Long> otherUserProductIds = getProductIds(otherUserPurchases);

		if (userProductIds.isEmpty() || otherUserProductIds.isEmpty()) {
			return 0.0; // No hay datos suficientes para calcular la similitud
		}

		// Calcular la intersección de productos entre los usuarios
		Set<Long> intersection = new HashSet<>(userProductIds);
		intersection.retainAll(otherUserProductIds);

		// Calcular la unión de productos entre los usuarios
		Set<Long> union = new HashSet<>(userProductIds);
		union.addAll(otherUserProductIds);

		// Calcular la similitud utilizando el coeficiente de Jaccard
		double similarity = (double) intersection.size() / union.size();

		return similarity;
	}

	private List<Long> getProductIds(List<ShoppingCartEntity> purchases) {
		return productCartRepository.findProductIdsByShoppingCarts(purchases);
	}

	// filtrado basado en contenido, concretamente item-to-item mediante las
	// palabras clave de los productos
	// devuelve los productos similares
	@Override
	public List<ProductEntity> findSimilarProductsByTags(Long productId) {
		// Obtener las etiquetas del producto actual
		ProductEntity currentProduct = productRepository.findById(productId).orElse(null);
		if (currentProduct == null) {
			// Manejar el caso en que el producto no se encuentre
			return null;
		}
		List<String> tags = currentProduct.getTags();

		// Buscar productos que tengan al menos una etiqueta en común con el producto
		// actual
		List<ProductEntity> similarProducts = productRepository.findByTagsIn(tags);

		// Eliminar el producto actual de la lista de productos similares
		similarProducts.remove(currentProduct);

		return similarProducts;
	}

	// para cada par de productos de la base de datos calcular el porcentaje de
	// similaritud y guardarlo en la entidad simi
	@Override
	public void calcularSimiProductos() {
		// Obtener todos los productos de la base de datos
		List<ProductEntity> allProducts = productRepository.findAll();

		// Definir un umbral mínimo de similitud
		double umbralMinimoSimilitud = 0.5;

		// Iterar sobre cada par de productos para calcular la similitud
		for (int i = 0; i < allProducts.size(); i++) {
			ProductEntity productA = allProducts.get(i);
			for (int j = i + 1; j < allProducts.size(); j++) {
				ProductEntity productB = allProducts.get(j);

				// Calcular el porcentaje de similitud entre los productos
				double similarityPercentage = calcularPorcentajeSimilitud(productA, productB);

				// Si la similitud supera el umbral mínimo, guardar la similitud en la entidad
				// SimilarProductEntity
				if (similarityPercentage >= umbralMinimoSimilitud) {
					SimilarProductEntity similarProduct = new SimilarProductEntity();
					similarProduct.setProd1(productA);
					similarProduct.setProd2(productB);
					similarProduct.setSimilarity(similarityPercentage);

					// Guardar la entidad SimilarProductEntity en la base de datos
					similarProductRepository.save(similarProduct);
				}
			}
		}
	}

	// Método para calcular el porcentaje de similitud entre dos productos basado en
	// etiquetas

	public double calcularPorcentajeSimilitud(ProductEntity productA, ProductEntity productB) {
		// Obtener las etiquetas de ambos productos
		List<String> tagsA = productA.getTags();
		List<String> tagsB = productB.getTags();

		// Calcular el número de etiquetas comunes
		List<String> commonTags = new ArrayList<>(tagsA);
		commonTags.retainAll(tagsB);
		int numCommonTags = commonTags.size();

		// Calcular el total de etiquetas únicas en ambos productos
		Set<String> uniqueTags = new HashSet<>();
		uniqueTags.addAll(tagsA);
		uniqueTags.addAll(tagsB);
		int totalUniqueTags = uniqueTags.size();
		double similarityPercentage = (double) numCommonTags / totalUniqueTags * 100.0;

		return similarityPercentage;
	}
}
