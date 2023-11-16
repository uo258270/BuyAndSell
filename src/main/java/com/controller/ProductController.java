package com.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.entity.ProductEntity;
import com.entity.UserEntity;
import com.service.ProductsService;
import com.service.RecommendationSystemService;
import com.service.UserService;
import com.validators.AddOfferValidator;

@RestController
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private ProductsService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private AddOfferValidator addOfferValidator;

	@Autowired
	private RecommendationSystemService recommendedService;

	public ProductController(ProductsService productService, UserService userService,
			AddOfferValidator addOfferValidator) {
		super();
		this.productService = productService;
		this.userService = userService;
		this.addOfferValidator = addOfferValidator;
	}

	@GetMapping("/own/{userId}")
	public String listOwnProducts(@PathVariable Long userId, Model model) {
		List<ProductEntity> prod = productService.getProducts(userId);
		model.addAttribute("products", prod);
		return "/product/listOwn";
	}

	@GetMapping("/exceptOwn/{userId}")
	public String listAllProductsExceptOwn(@PathVariable Long userId, Model model) {
		List<ProductEntity> prod = productService.getProductsExceptOwn(userId);
		model.addAttribute("products", prod);
		return "/product/listProducts";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String setProduct(Model model, Principal principal, @Validated ProductEntity product, BindingResult result)
			throws Exception {
		addOfferValidator.validate(product, result);
		if (result.hasErrors()) {
			return "redirect:/add";
		}

		product.setProductDate(new Date());
		String email = principal.getName();
		UserEntity user = userService.findByEmail(email);
		product.setUser(user);
		productService.addProduct(product);

		return "redirect:/product/own/" + user.getUserId();
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String getProduct(Model model) {
		model.addAttribute("product", new ProductEntity());
		return "product/add";
	}

	@RequestMapping("/product/delete/{id}")
	public String deleteProduct(@PathVariable Long id) throws Exception {
		productService.deleteProduct(id);
		return "product/listOwn";
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String editProductForm(@PathVariable Long id, Model model) throws Exception {
		ProductEntity product = productService.findById(id);
		model.addAttribute("product", product);

		return "product/editProduct";
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
	public String editProduct(@PathVariable Long id, @ModelAttribute("product") ProductEntity editedProduct) {

		productService.updateProduct(editedProduct);
		Long userId = editedProduct.getUser().getUserId();
		return "redirect:/product/own/" + userId;

	}

	@GetMapping("/detail/{productId}")
	public String showProductDetail(@PathVariable Long productId, Model model) throws Exception {
		ProductEntity product = productService.findById(productId);

		if (product == null) {
			throw new Exception("El producto no existe");
		}
		double averageRating = productService.calculateAverageRating(productId);
        model.addAttribute("product", product);
        model.addAttribute("averageRating", averageRating);

		return "product/detail";
	}

	@GetMapping("/product/recommendedReview/{userId}")
	public String listRecommendedProductsByReview(Model model, @PathVariable Long userId) {
		List<ProductEntity> recommended = recommendedService.getProductsBySimilarReviewUsers(userId);
		model.addAttribute("recommendedReviews", recommended);
		return "/product/listProducts";
	}

	@GetMapping("/product/recommended/{userId}")
	public String listRecommendedProductsByCart(Model model, @PathVariable Long userId) {
		List<ProductEntity> recommended = recommendedService.getProductsBySimilarUserCarts(userId);
		model.addAttribute("recommended", recommended);
		return "/product/listProducts";
	}

	@GetMapping("/product/popular")
	public String listPopularProducts(Model model) throws Exception {
		try {
			List<ProductEntity> popular = recommendedService.getMostPopularProducts();
			model.addAttribute("popular", popular);
			return "/product/listProducts";
		} catch (Exception e) {
			throw new Exception("Error while trying to list popular products", e);
		}
	}

	@GetMapping("/product/topRated")
	public String listTopRatedProducts(Model model) throws Exception {
		try {
			List<ProductEntity> rated = recommendedService.getTopRatedProducts();
			model.addAttribute("rated", rated);
			return "/product/listProducts";
		} catch (Exception e) {
			throw new Exception("Error while trying to list popular products", e);
		}
	}

}
