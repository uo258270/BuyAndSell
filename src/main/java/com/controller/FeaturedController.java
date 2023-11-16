package com.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.entity.FeaturedProductEntity;
import com.entity.ProductEntity;
import com.entity.UserEntity;
import com.service.FeaturedProductService;
import com.service.UserService;

@RestController
@RequestMapping("/featured")
public class FeaturedController {
	
	@Autowired
	private FeaturedProductService featuredService;

	@Autowired
	private UserService userService;


	
	public FeaturedController(FeaturedProductService featuredService, UserService userService) {
		super();
		this.featuredService = featuredService;
		this.userService = userService;
		
	}
	
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String setFavProduct(Model model, Principal principal, @Validated ProductEntity prod, BindingResult result) throws Exception {
		try {
			featuredService.addFeatured(prod);
			return "redirect:/featured/listByUser";
		}
		catch(Exception e) {
			throw new Exception("Product cannot be added to favourites");
		}
		
	}
	
	@RequestMapping("/listByUser")
	public String getListByUser(Model model, Principal principal) throws Exception {
		String email = principal.getName();
		UserEntity user = userService.findByEmail(email);
		model.addAttribute("user", user);
		model.addAttribute("featuredList", featuredService.findByUser(user));
		return "/featured/listByUser";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public String deleteFavProduct(Model model, Principal principal, @Validated FeaturedProductEntity prod, BindingResult result) throws Exception {
		try {
			featuredService.deleteFeaturedProduct(prod);
			return "redirect:/featured/listByUser";
		}
		catch(Exception e) {
			throw new Exception("Product cannot be deleted from favourites");
		}
		
	}
}
