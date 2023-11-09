package com.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entity.ProductEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.exception.NotFoundException;
import com.service.ProductsService;
import com.service.ShoppingCartService;
import com.service.UserService;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

	@Autowired
	ShoppingCartService shoppingCartService;
	
	@Autowired 
	UserService userService;
	
	@Autowired
	ProductsService productService;

	@PostMapping("/addProduct")
	public String addItem(@RequestBody ProductEntity product) throws Exception {
		try {
			shoppingCartService.addProduct(product);
			return "redirect:/cart";
		} catch (Exception e) {
			throw new Exception("Failed to add product to cart.");
		}
	}

	@DeleteMapping("/removeProduct")
	public String removeItem(@RequestBody ProductEntity product) throws Exception {
		try {
			shoppingCartService.deleteProductFromShoppingCart(product);
			return "redirect:/cart";
		} catch (Exception e) {
			throw new Exception("Failed to remove item from cart.");
		}
	}

	@GetMapping("/listShoppingsFromUser")
	public String listComprasByUser(@RequestBody UserEntity user, Model model) {

		List<ShoppingCartEntity> list = shoppingCartService.findShoppingsByUser(user);
		model.addAttribute("list", list);
		return "/cart/listShoppings";

	}

	@PostMapping("/clearCart")
	public String clearCart(@RequestParam ShoppingCartEntity cart) {
		try {
			shoppingCartService.clear(cart);
			return "redirect:/cart";
		} catch (NotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}
	}

	@GetMapping("")
	public String getCart(Model model) throws Exception {
		try {
			Optional<ShoppingCartEntity> cartOpt = shoppingCartService.getCart();
			ShoppingCartEntity cart = cartOpt.get();
			model.addAttribute("cart", cart);
			return "/cart/show";
		} catch (Exception e) {
			throw new Exception();
		}
	}
	
	@RequestMapping("/buy")
	public String buyProduct(@PathVariable Long id, Principal principal) throws Exception {
		String email = principal.getName();
		UserEntity user = userService.findByEmail(email);
		if(userService.updateMoney(user.getUserId(), shoppingCartService.getTotalOrderPrice())) {
			shoppingCartService.buyShoppingCart();
			return "redirect:/home";
		}	
		return "/noMoney";	
	}

}
