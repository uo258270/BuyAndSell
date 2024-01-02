package com.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.entity.ProductEntity;
import com.entity.UserEntity;
import com.service.ProductsService;
import com.service.RecommendationSystemService;
import com.service.UserService;
import com.validators.AddOfferValidator;

@Controller
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
			AddOfferValidator addOfferValidator, RecommendationSystemService recommendedService) {
		super();
		this.productService = productService;
		this.userService = userService;
		this.addOfferValidator = addOfferValidator;
		this.recommendedService = recommendedService;
	}

	@GetMapping("/own")
	public String listOwnProducts(Model model, Principal principal) throws Exception {
		String email = principal.getName();
		UserEntity user = userService.findByEmail(email);
		List<ProductEntity> prod = productService.getProducts(user.getUserId());
		List<ProductEntity> soldProducts = productService.getSoldProducts(user.getUserId());
		model.addAttribute("products", prod);
		model.addAttribute("soldProducts", soldProducts);
		return "product/listOwn";

	}

	@GetMapping("/exceptOwn")
	public String listAllProductsExceptOwn(Model model, Principal principal) throws Exception {
		String email = principal.getName();
		UserEntity user = userService.findByEmail(email);
		List<ProductEntity> prod = productService.getProductsExceptOwn(user.getUserId());
		model.addAttribute("products", prod);
		return "product/listProducts";
	}

	@GetMapping("/allRecommendedProducts")
	public String listAllRecommendedProducts(Model model, Principal principal) throws Exception {
		if (principal == null) {
			return "redirect:/login";
		}

		String email = principal.getName();
		UserEntity user = userService.findByEmail(email);

		List<ProductEntity> productsExceptOwn = productService.getProductsExceptOwn(user.getUserId());
		model.addAttribute("products", productsExceptOwn);

		List<ProductEntity> recommendedByReview = recommendedService.getProductsBySimilarReviewUsers(user.getUserId());
		model.addAttribute("recommendedReviews", recommendedByReview);

		List<ProductEntity> recommendedByCart = recommendedService.getProductsBySimilarUserCarts(user.getUserId());
		model.addAttribute("recommended", recommendedByCart);

		List<ProductEntity> popular = recommendedService.getMostPopularProducts();
		model.addAttribute("popular", popular);

		List<ProductEntity> topRated = recommendedService.getTopRatedProducts();
		model.addAttribute("rated", topRated);

		return "/product/listProducts";
	}

	@GetMapping("/add")
	public String getProduct(Model model) {
		model.addAttribute("product", new ProductEntity());
		return "product/add";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String setProduct(@Validated ProductEntity product, BindingResult result, Model model, Principal principal)
			throws Exception {
		addOfferValidator.validate(product, result);
		if (result.hasErrors()) {
			return "product/add";
		}

		product.setProductDate(new Date());
		String email = principal.getName();
		UserEntity user = userService.findByEmail(email);
		product.setUser(user);
		productService.addProduct(product);

		return "redirect:/product/own";
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
		return "redirect:/product/own/";

	}

	@RequestMapping("/detail")
	public String showProductDetail(Long productId, Model model, Principal principal) throws Exception {
		ProductEntity product = productService.findById(productId);

		if (product == null) {
			throw new Exception("El producto no existe");
		}
		double averageRating = 0.0;
		if (!product.getReviews().isEmpty()) {
			averageRating = productService.calculateAverageRating(productId);

		}
		System.out.println("Principal name: " + principal.getName());
		System.out.println("Product user name: " + product.getUser().getName());


		model.addAttribute("product", product);
		model.addAttribute("principal", principal);
		model.addAttribute("averageRating", averageRating);

		return "product/detail";
	}

	@GetMapping("/search")
	public String showSearchPage(Model model) {
		return "product/search";
	}

	@GetMapping("/search/results")
	public String searchProducts(@RequestParam(name = "searchTerm", required = false) String searchTerm, Model model) {
		if (searchTerm != null && !searchTerm.isBlank()) {
			List<ProductEntity> searchResults = productService.searchProducts(searchTerm);
			model.addAttribute("searchTerm", searchTerm);
			model.addAttribute("searchResults", searchResults);
		}

		return "product/searchResults";
	}
}
