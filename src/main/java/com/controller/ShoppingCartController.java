package com.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.entity.ShoppingCartEntity;
import com.exception.InvalidStockException;
import com.exception.NotEnoughMoney;
import com.exception.NotFoundException;
import com.exception.ProductAlreadySoldException;
import com.service.ProductService;
import com.service.ShoppingCartService;
import com.service.UserService;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

	
	@Autowired
	ShoppingCartService shoppingCartService;

	@Autowired
	UserService userService;

	@Autowired
	ProductService productService;

	public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService,
			ProductService productService) {
		super();
		this.shoppingCartService = shoppingCartService;
		this.userService = userService;
		this.productService = productService;
	}

	@PostMapping("/addProduct")
	public String addItem(@RequestParam Long productId, @RequestParam int quantity, RedirectAttributes attr) {
		try {
			ShoppingCartEntity sho = shoppingCartService.incrementProductQuantity(productId);
		}
		catch (InvalidStockException e) {
			attr.addFlashAttribute("aviso", "No hay suficiente stock");
		}
		return "redirect:/cart";
	}

	@PostMapping("/clearCart")
	public String clearCart(Model model) {
		try {
			shoppingCartService.clear();
			model.addAttribute("cartCleared", true);
			return "redirect:/cart";
		} catch (NotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}
	}

	@GetMapping("")
	public String getCart(Model model) throws Exception {
		try {
			ShoppingCartEntity cartOpt = shoppingCartService.getCart();
			ShoppingCartEntity cart = cartOpt;
			model.addAttribute("cart", cart);
			return "/cart/show";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "No se pudo cargar el carrito");
			return "/error";
		} 
	}

	@RequestMapping("/buy")
	public String buyProduct(Principal principal, Model model) throws Exception {
	    try {
	        shoppingCartService.buyShoppingCart(principal.getName());
	        return "/cart/thankYou";
	    } catch (NotEnoughMoney e) {
	        model.addAttribute("errorMessage", "No tienes suficiente dinero para realizar la compra.");
	        return "/error";
	    } catch (ProductAlreadySoldException e) {
	        model.addAttribute("errorMessage", "El producto ya ha sido vendido.");
	        return "/error";
	    }
	}
	
	

	@PostMapping("/updateQuantity")
	public String updateQuantity(@RequestParam Long productId, @RequestParam int quantity, @RequestParam String action, Model model) throws Exception {
	    try {
	        if ("increment".equals(action)) {
	            shoppingCartService.incrementProductQuantity(productId);
	        } else if ("decrement".equals(action)) {
	            shoppingCartService.decrementProductQuantity(productId);
	        }

	        return "redirect:/cart";
	    } catch (InvalidStockException e) {
	        model.addAttribute("errorMessage", "No hay suficiente stock disponible para este producto.");
	        return "/error";
	    }
	}


	
}
