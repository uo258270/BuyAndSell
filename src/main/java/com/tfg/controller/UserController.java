package com.tfg.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tfg.entity.ProductEntity;
import com.tfg.entity.UserEntity;
import com.tfg.entity.enums.RoleEnum;
import com.tfg.service.PayPalService;
import com.tfg.service.ProductService;
import com.tfg.service.ShoppingCartService;
import com.tfg.service.UserService;
import com.tfg.validators.SignUpFormValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private UserService usersService;

	@Autowired
	private SignUpFormValidator signUpFormValidator;
	
	@Autowired
	private ShoppingCartService cartService;

	@Autowired
	private ProductService productService;
	
	@Autowired
	private PayPalService payPalService;

	@Value("${paypal.client-id}")
	private String clientId;
	
	@Value("${paypal.client-secret}")
	private String clientSecret;
	
	

	public UserController(HttpSession httpSession, UserService usersService, SignUpFormValidator signUpFormValidator,
			ShoppingCartService cartService, ProductService productService, PayPalService payPalService) {
		super();
		this.httpSession = httpSession;
		this.usersService = usersService;
		this.signUpFormValidator = signUpFormValidator;
		this.cartService = cartService;
		this.productService = productService;
		this.payPalService = payPalService;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		return "redirect:/product/allRecommendedProducts";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(Model model) {
		model.addAttribute("userEntity", new UserEntity());
		return "signup";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(UserEntity userEntity, BindingResult result, Model model) throws Exception {
		signUpFormValidator.validate(userEntity, result);
		if (result.hasErrors()) {
			return "signup";
		}
		userEntity.setRole(RoleEnum.ROLE_USER);
		usersService.addUser(userEntity);
		usersService.autoLogin(userEntity.getEmail(), userEntity.getPasswordConfirm());
		return "redirect:/home";
	}

	@GetMapping(value = "/login")
	public String login(Model model) {
		return "user/login";
	}

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String home(Model model, Principal principal) throws Exception {
		if (principal != null) {
			UserEntity user = usersService.findByEmail(principal.getName());
			httpSession.setAttribute("money", user.getMoney());
			httpSession.setAttribute("email", user.getEmail());

			List<ProductEntity> productList = productService.getProductsExceptOwn(user.getUserId());
			model.addAttribute("productList", productList);
			//return "user/home";
			return "redirect:/product/allRecommendedProducts";
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public String list(Model model) {

		model.addAttribute("usersList", usersService.getStandardUsers());
		return "user/list";
	}

	@RequestMapping(value = "/user/delete", method = RequestMethod.POST)
	public String delete(Model model, @RequestParam Map<String, String> deletedUsers) throws Exception {

		for (Map.Entry<String, String> entry : deletedUsers.entrySet()) {
			usersService.deleteUser(usersService.findByEmail(entry.getValue()).getUserId());
		}

		model.addAttribute("usersList", usersService.getStandardUsers());

		return "user/list";
	}

	@GetMapping("/profile")
	public String profile(Model model, Principal principal) throws Exception {
		String email = principal.getName();
		UserEntity user = usersService.findByEmail(email);
		model.addAttribute("user", user);
		return "user/profile";
	}

	 @PostMapping("/paypal/payment")
	    public String makePayment(@RequestParam("amount") double amount, Principal principal, HttpServletRequest request) {
	       
	        try {
	            Map<String, Object> order = new HashMap<>();
	            order.put("intent", "sale");
	            
	            Map<String, Object> amountMap = new HashMap<>();
	            amountMap.put("total", String.format("%.2f", amount));
	            amountMap.put("currency", "USD");
	            order.put("amount", amountMap);
	            
	            order.put("return_url", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/paypal/confirm");
	            order.put("cancel_url", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/paypal/cancel");
	            
	            String orderId = payPalService.createOrder(order);
	            
	            return "redirect:" + payPalService.getApprovalUrl(orderId);
	        } catch (Exception e) {
	            return "/error";
	        }
	    }
	 
	@RequestMapping(value = "/user/addMoney", method = RequestMethod.POST)
	public String addMoney(Double amount, Principal principal, Model model){
		String email = principal.getName();
		 if (amount <= 0) {
		        model.addAttribute("errorMessage", "Por favor, ingresa una cantidad válida.");
		        return "/error";
		    }
		try {
			usersService.addMoney(email, amount);
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Error processing the transaction");
	        return "/error";
		}

		return "redirect:/profile";
	} 
	
	
	@ModelAttribute
	public void loadCurrentUser(Model model, Principal p) throws Exception {
		if (p != null) {
			model.addAttribute("currentUser", usersService.findByEmail(p.getName()));
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
