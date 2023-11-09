package com.controller;

import java.security.Principal;
import java.util.Date;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.PathVariable;
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
			return "redirect:product/add";
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
	
	
	@RequestMapping("/product/delete/{id}")
	public String deleteProduct(@PathVariable Long id) throws Exception {
		productService.deleteProduct(id);
		return "redirect:/product/listByUser";
	}
	

	

}
