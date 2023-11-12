package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.ReviewEntity;
import com.exception.UnauthorizedException;
import com.service.ReviewService;


@RestController
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@GetMapping("/reviews/{productId}")
	public String verReviews(@PathVariable Long productId, Model model) {

		List<ReviewEntity> reviews = reviewService.getReviewsByProductId(productId);
		model.addAttribute("reviews", reviews);
		return "reviews/verReviews";
	}
	
	@PostMapping("/reviews/add")
    public String addReview(Model model, ReviewEntity review) throws UnauthorizedException {
        reviewService.addReview(review);
        return "reviews/formulario-review";
    }
	
	
	@PostMapping("/reviews/delete/{id}")
	public String deleteReview(@PathVariable Long id) {
	    // Aquí puedes realizar validaciones y eliminar la reseña de la base de datos
	    reviewService.deleteReview(id);
	    return "redirect:/productos"; // Redirige a la página de productos u otra página
	}
}
