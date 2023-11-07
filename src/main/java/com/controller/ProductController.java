package com.controller;

import java.security.Principal;
import java.util.Date;
import java.util.LinkedList;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entity.ProductEntity;
import com.entity.UserEntity;
import com.service.ProductsService;
import com.service.UserService;
import com.validators.AddOfferValidator;



@RestController
public class ProductController {
	
	@Autowired
	private ProductsService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private AddOfferValidator addOfferValidator;
	

	
	public ProductController(ProductsService productService, UserService userService,
			AddOfferValidator addOfferValidator) {
		super();
		this.productService = productService;
		this.userService = userService;
		this.addOfferValidator = addOfferValidator;
	}

	@RequestMapping(value = "/product/add", method = RequestMethod.POST)
	public String setProduct(Model model, Principal principal, @Validated ProductEntity product, BindingResult result) throws Exception {
		addOfferValidator.validate(product, result);
		if (result.hasErrors()) {
			return "product/add";
		}

		product.setProductDate(new Date());
		String email = principal.getName();
		UserEntity user = userService.findByEmail(email);
		product.setUser(user);
		productService.addProduct(product);

		
		return "redirect:/product/listByUser";
	}
	
	@RequestMapping(value = "/product/add", method = RequestMethod.GET)
	public String getProduct(Model model) {
		model.addAttribute("product", new ProductEntity());
		return "product/add";
	}
	
	
	@RequestMapping("/offer/delete/{id}")
	public String deleteOffer(@PathVariable Long id) throws Exception {
		productService.deleteProduct(id);
		return "redirect:/offer/listByUser";
	}
	
	@RequestMapping("/offer/buy/{id}")
	public String buyOffer(@PathVariable Long id, Principal principal) throws Exception {
		String email = principal.getName();
		UserEntity user = userService.findByEmail(email);
		ProductEntity product = productService.findById(id);
//		if(userService.updateMoney(user.getId(), offer.getPrice())) {
//			offersService.buyOffer(id, user);
//			httpSession.setAttribute("money", user.getMoney());
//			return "redirect:/offer/search";
//		}	
//		
//		return "redirect:/offer/noMoney";	
		return email;
	}
	

}
