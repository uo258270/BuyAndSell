package com.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entity.FeaturedProductEntity;
import com.entity.ProductEntity;
import com.entity.UserEntity;
import com.service.FeaturedProductService;
import com.service.ProductsService;
import com.service.UserService;

@Controller
@RequestMapping("/featured")
public class FeaturedController {
	
	@Autowired
	private FeaturedProductService featuredService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductsService productService;


	
	public FeaturedController(FeaturedProductService featuredService, UserService userService, ProductsService productService) {
		super();
		this.featuredService = featuredService;
		this.userService = userService;
		this.productService =productService;
		
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
			return "/featured/listByUser";
		}
		catch(Exception e) {
			throw new Exception("Product cannot be added to favourites");
		}
		
	}


	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteFavProduct(Model model, Principal principal,  Long id) throws Exception {
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
		try {
			String email = principal.getName();
			UserEntity user = userService.findByEmail(email);
			model.addAttribute("user", user);
			model.addAttribute("featuredList", featuredService.findByUser(user));
			return "featured/listByUser";
		}
		catch(Exception e) {
			throw new Exception("List is empty");
		}
		
	}
	
	
}
