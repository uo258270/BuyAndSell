package com.tfg.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tfg.entity.ShoppingCartEntity;
import com.tfg.exception.InvalidStockException;
import com.tfg.exception.NotEnoughMoney;
import com.tfg.exception.ProductAlreadySoldException;
import com.tfg.service.FeaturedProductService;
import com.tfg.service.ProductService;
import com.tfg.service.ShoppingCartService;
import com.tfg.service.UserService;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

	@Autowired
	ShoppingCartService shoppingCartService;

	@Autowired
	UserService userService;

	@Autowired
	ProductService productService;
	
	@Autowired
	FeaturedProductService featuredService;

	public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService,
			ProductService productService, FeaturedProductService featuredService) {
		super();
		this.shoppingCartService = shoppingCartService;
		this.userService = userService;
		this.productService = productService;
		this.featuredService  = featuredService;
	}

	@PostMapping("/addProduct")
	public String addItem(@RequestParam Long productId, @RequestParam int quantity, RedirectAttributes attr) {
		try {
			ShoppingCartEntity sho = shoppingCartService.incrementProductQuantity(productId, quantity);
		} catch (InvalidStockException e) {
			attr.addFlashAttribute("aviso", "No hay suficiente stock");
		}
		return "redirect:/cart";
	}

	@PostMapping("/clearCart")
	public String clearCart(Model model) {

		shoppingCartService.clear();
		model.addAttribute("cartCleared", true);
		return "redirect:/cart";

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
	public String updateQuantity(@RequestParam Long productId, @RequestParam int quantity, @RequestParam String action,
			Model model) throws Exception {
		try {
			if ("increment".equals(action)) {
				shoppingCartService.incrementProductQuantity(productId, quantity);
			} else if ("decrement".equals(action)) {
				shoppingCartService.decrementProductQuantity(productId, quantity);
			}

			return "redirect:/cart";
		} catch (InvalidStockException e) {
			model.addAttribute("errorMessage", "No hay suficiente stock disponible para este producto.");
			return "/error";
		}
	}

	@ModelAttribute
	public void loadCurrentUser(Model model, Principal p) throws Exception {
		if (p != null) {
			model.addAttribute("currentUser", userService.findByEmail(p.getName()));
			model.addAttribute("favoriteProducts",  featuredService.findByUser(userService.findByEmail(p.getName())));
			model.addAttribute("cart", shoppingCartService.getCart());
		} else {
			model.addAttribute("currentUser", null);
			model.addAttribute("favoriteProducts", null);
		}
	}
	

}
