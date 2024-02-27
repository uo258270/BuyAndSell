package com.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.entity.FeaturedProductEntity;
import com.entity.ProductEntity;
import com.entity.UserEntity;
import com.service.FeaturedProductService;
import com.service.ProductService;
import com.service.ShoppingCartService;
import com.service.UserService;

@Controller
@RequestMapping("/featured")
public class FeaturedController {
	
	@Autowired
	private FeaturedProductService featuredService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;
	
	@Autowired
	private ShoppingCartService cartService;


	

	
	public FeaturedController(FeaturedProductService featuredService, UserService userService,
			ProductService productService, ShoppingCartService cartService) {
		super();
		this.featuredService = featuredService;
		this.userService = userService;
		this.productService = productService;
		this.cartService = cartService;
	}


	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addFavProduct(Model model, Principal principal, @Validated ProductEntity prod, BindingResult result) throws Exception {
		try {
			String email = principal.getName();
			UserEntity user = userService.findByEmail(email);
			featuredService.addFeatured(prod, user);
			model.addAttribute("user", user);
			List<FeaturedProductEntity> featuredList = featuredService.findByUser(user);
			model.addAttribute("featuredList", featuredList);
			return "redirect:/featured/listByUser";
		}
		catch(Exception e) {
			 model.addAttribute("errorMessage", "El producto ya ha sido añadido a favoritos");
		        return "/error";
		}
		
	}


	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteFavProduct(Model model, Principal principal,  @RequestParam Long id) throws Exception {
		try {
			String email = principal.getName();
			UserEntity user = userService.findByEmail(email);
			FeaturedProductEntity prod = featuredService.getFeaturedById(id);
			featuredService.deleteFeaturedProduct(prod);
			model.addAttribute("user", user);
			model.addAttribute("featuredList", featuredService.findByUser(user));
			return "/featured/listByUser";
		}
		catch(Exception e) {
			throw new Exception("Product cannot be deleted from favourites");
		}
		
	}
	
	@RequestMapping(value = "/listByUser")
	public String listFavProducts(Model model, Principal principal) throws Exception {

			String email = principal.getName();
			UserEntity user = userService.findByEmail(email);
			model.addAttribute("user", user);
			model.addAttribute("featuredList", featuredService.findByUser(user));
			return "featured/listByUser";
		
	}
	
	@GetMapping("/featured/check")
	@ResponseBody
	public boolean checkIfProductIsFavorite(@RequestParam Long productId) {
	    
	    return productService.checkIfProductIsFavorite(productId);
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
