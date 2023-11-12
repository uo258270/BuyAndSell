package com.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.entity.UserEntity;
import com.repository.ProductRepository;
import com.repository.UserRepository;
import com.service.RecommendationSystemService;

@Service
public class RecommendationSystemServiceImpl implements RecommendationSystemService{
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private UserRepository userRepository;

	
	//mas populares
	@Override
	public List<ProductEntity> getMostPopularProducts() throws Exception {
        List<ProductEntity> prods = productRepository.findMostPopularProductsThisMonthLimited();
        if(prods!=null) {
        	return prods;
        }else {
        	throw new Exception("There are no popular products");
        }
        
    }
	
	//------------------------------------------------------------------------
	//recomendos par el usuario basados en gustos en comun con otros usuarios
	@Override
	public List<ProductEntity> getCollaborativeFilteringRecommendations(Long userId) {
        UserEntity user = userRepository.findByUserId(userId);

        // Obtener todos los usuarios que han dado valoraciones
        List<UserEntity> usersWithRatings = userRepository.findAll()
                .stream()
                .filter(u -> !u.equals(user) && !u.getRatings().isEmpty())
                .collect(Collectors.toList());

        // Calcular similitud entre usuarios usando la correlación de Pearson
        List<UserSimilarity> userSimilarities = usersWithRatings.stream()
                .map(otherUser -> new UserSimilarity(otherUser, calculatePearsonCorrelation(user, otherUser)))
                .collect(Collectors.toList());

        // Filtrar usuarios similares y obtener productos recomendados
        List<ProductEntity> recommendedProducts = userSimilarities.stream()
                .sorted(Comparator.comparingDouble(UserSimilarity::getSimilarity).reversed())
                .flatMap(similarity -> similarity.getUser().getRatings().stream())
                .filter(rating -> !user.getRatings().contains(rating))
                .map(ReviewEntity::getProduct)
                .distinct()
                .collect(Collectors.toList());

        return recommendedProducts;
    }

	//Correlacion de Pearson 
    private double calculatePearsonCorrelation(UserEntity user1, UserEntity user2) {
    	 List<ReviewEntity> ratings1 = user1.getRatings();
         List<ReviewEntity> ratings2 = user2.getRatings();

         // Calcular la media de las calificaciones de cada usuario
         double mean1 = calculateMean(ratings1);
         double mean2 = calculateMean(ratings2);

         // Calcular la suma de los productos de las diferencias entre las calificaciones y las medias
         double numerator = 0.0;
         double denominator1 = 0.0;
         double denominator2 = 0.0;

         for (ReviewEntity rating1 : ratings1) {
             for (ReviewEntity rating2 : ratings2) {
            	 //Compara si las calificaciones pertenecen al mismo producto, solo queremos comparar calificaciones para el mismo producto.
                 if (rating1.getProduct().equals(rating2.getProduct())) {
                	 //Estas diferencias indican cuánto se desvían las calificaciones individuales de sus respectivas medias.
                     double diff1 = rating1.getRating() - mean1;
                     double diff2 = rating2.getRating() - mean2;

                     //El numerador de la fórmula de correlación de Pearson es la suma acumulativa del producto de las diferencias (diff1 y diff2) para cada par de calificaciones
                     numerator += diff1 * diff2;
                     //Los denominadores de la fórmula de correlación de Pearson son las sumas acumulativas de los cuadrados de las diferencias (diff1 y diff2)
                     denominator1 += Math.pow(diff1, 2);
                     denominator2 += Math.pow(diff2, 2);
                 }
             }
         }

         // Evitar la división por cero
         if (denominator1 == 0 || denominator2 == 0) {
             return 0.0;
         }

         // Calcular la correlación de Pearson
         return numerator / (Math.sqrt(denominator1) * Math.sqrt(denominator2));
     }

    //media
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
	//mejores valorados
    @Override
    public List<ProductEntity> getTopRatedProducts() throws Exception {
        List<ProductEntity> prods = productRepository.findAllByOrderByAverageRatingDesc();
        if(prods!=null) {
        	return prods;
        }else {
        	 throw new Exception("there are no best rated products");
        }
       
    }
	

}
