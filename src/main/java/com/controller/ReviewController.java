package com.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.entity.UserEntity;
import com.exception.NotFoundException;
import com.exception.UnauthorizedException;
import com.service.ProductService;
import com.service.ReviewService;
import com.service.ShoppingCartService;
import com.service.UserService;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;
	
	@Autowired
	private ShoppingCartService cartService;

	

	public ReviewController(ReviewService reviewService, UserService userService, ProductService productService,
			ShoppingCartService cartService) {
		super();
		this.reviewService = reviewService;
		this.userService = userService;
		this.productService = productService;
		this.cartService = cartService;
	}


	@GetMapping("/")
	public String verReviews(@RequestParam Long productId, Model model) {

		List<ReviewEntity> reviews = reviewService.getReviewsByProductId(productId);
		model.addAttribute("reviews", reviews);
		model.addAttribute("review", new ReviewEntity());
		return "reviews/listReview";
	}

	
	@PostMapping("/add")
	public String addReview(@RequestParam Long productId, Model model, ReviewEntity review, Principal principal) throws Exception {
		
			try {
				String email = principal.getName();
				UserEntity user = userService.findByEmail(email);
				review.setUserEntity(user);
				ProductEntity product = productService.findById(productId);
				review.setProduct(product);
				reviewService.addReview(review);
				model.addAttribute("productId", productId);
				return "redirect:/product/detail/"+ productId;

			} catch (UnauthorizedException e) {
				model.addAttribute("errorMessage", e.getMessage());
				return "/error";
			} catch (NotFoundException e) {
				model.addAttribute("errorMessage", e.getMessage());
				return "/error";
			}

		
	}


	@RequestMapping(value = "/delete/{id}", method = {RequestMethod.GET, RequestMethod.POST})
	public String deleteReview(@PathVariable Long id, Model model, @RequestParam Long productId) {
	    try {
	        reviewService.deleteReview(id);
	        return "redirect:/product/detail/" + productId;
	    } catch (NotFoundException e) {
	        model.addAttribute("errorMessage", e.getMessage());
	        return "/error";
	    }
	}

	
	@ModelAttribute
	public void loadCurrentUser(Model model, Principal p) throws Exception {
		if (p != null) {
			model.addAttribute("currentUser", userService.findByEmail(p.getName()));
		}
		else {
			model.addAttribute("currentUser", null);
		}
	}
	
	@ModelAttribute
	public void loadCart(Model model, Principal p) throws Exception {
			model.addAttribute("cart", cartService.getCart());
	}
}
