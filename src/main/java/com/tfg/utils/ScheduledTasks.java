package com.tfg.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tfg.service.RecommendationSystemService;

@Component
public class ScheduledTasks {
	
    private final RecommendationSystemService recommendationSystemService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    public ScheduledTasks(RecommendationSystemService recommendationSystemService) {
        this.recommendationSystemService = recommendationSystemService;
    }
    
    @Value("${cron-calculo-similitud}")
    private String cronCalculoSimilitud;

    @Scheduled(cron = "${cron-calculo-similitud}")
    public void calcularSimiProductos() {
        LOGGER.info("Ejecutando calcularSimiProductos...");
        recommendationSystemService.calcularSimiProductos();
        LOGGER.info("Tarea calcularSimiProductos ejecutada.");
    }

    @Scheduled(cron = "${cron-calculo-similitud}")
    public void calcularProductosPorComprasSimilares() {
        LOGGER.info("Ejecutando calcularProductosPorComprasSimilares...");
        recommendationSystemService.executeDailySimilarityCalculation();
        LOGGER.info("Tarea calcularProductosPorComprasSimilares ejecutada.");
    }

    @Scheduled(cron = "${cron-calculo-similitud}")
    public void calcularProductosPorReseñas() {
        LOGGER.info("Ejecutando calcularProductosPorReseñas...");
        recommendationSystemService.executeDailySimilarityCalculationReviews();
        LOGGER.info("Tarea calcularProductosPorReseñas ejecutada.");
    }
}
