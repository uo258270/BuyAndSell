package com.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.entity.ProductEntity;
import com.entity.ShoppingCartEntity;
import com.entity.UserEntity;
import com.exception.InvalidStockException;
import com.exception.NotEnoughMoney;
import com.exception.NotFoundException;
import com.exception.ProductAlreadySoldException;
import com.service.ProductsService;
import com.service.ShoppingCartService;
import com.service.UserService;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

	private static final Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);

	@Autowired
	ShoppingCartService shoppingCartService;

	@Autowired
	UserService userService;

	@Autowired
	ProductsService productService;

	public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService,
			ProductsService productService) {
		super();
		this.shoppingCartService = shoppingCartService;
		this.userService = userService;
		this.productService = productService;
	}

	@PostMapping("/addProduct")
	public String addItem(@RequestParam Long productId, @RequestParam int quantity, RedirectAttributes attr) {
		try {
			shoppingCartService.incrementProductQuantity(productId);
		}
		catch (InvalidStockException e) {
			attr.addFlashAttribute("aviso", "No hay suficiente stock");
		}
		return "redirect:/cart";
	}

	@DeleteMapping("/removeProduct")
	public String removeItem(@RequestBody ProductEntity product) throws Exception {
		try {
			shoppingCartService.decrementProductQuantity(product.getProductId());
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
			Optional<ShoppingCartEntity> cartOpt = shoppingCartService.getCart();
			ShoppingCartEntity cart = cartOpt.get();
			model.addAttribute("cart", cart);
			return "/cart/show";
		} catch (Exception e) {
			throw new Exception();
		}
	}

	@RequestMapping("/buy")
	public String buyProduct(Principal principal, Model model) throws Exception {
	    try {
	    	//TODO no se actualiza el dinero despues de hacer una compra
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
