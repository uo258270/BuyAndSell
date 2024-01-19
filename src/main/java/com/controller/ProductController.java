package com.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.entity.UserEntity;
import com.entity.enums.CategoryEnum;
import com.exception.NotFoundException;
import com.exception.UpdateProductException;
import com.service.ImageService;
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
	private ImageService imageService;

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

		List<ProductEntity> recommendedByReview = recommendedService.getProductsBySimilarReviewUsers(user.getUserId());
		List<ProductEntity> recommendedByCart = recommendedService.getProductsBySimilarUserCarts(user.getUserId());
		List<ProductEntity> popular = recommendedService.getMostPopularProducts();
		List<ProductEntity> topRated = recommendedService.getTopRatedProducts();

		List<ProductEntity> allRecommendedProducts = new ArrayList<>();
		allRecommendedProducts.addAll(recommendedByReview);
		allRecommendedProducts.addAll(recommendedByCart);
		allRecommendedProducts.addAll(popular);
		allRecommendedProducts.addAll(topRated);

		List<ProductEntity> filteredProducts = new ArrayList<>();
		for (ProductEntity product : allRecommendedProducts) {
			if (product.getStock() > 0) {
				filteredProducts.add(product);
			}
		}
		model.addAttribute("products", filteredProducts);
		model.addAttribute("recommendedReviews", recommendedByReview);
		model.addAttribute("recommended", recommendedByCart);
		model.addAttribute("popular", popular);
		model.addAttribute("rated", topRated);
		return "/product/listProducts";
	}

	@GetMapping("/add")
	public String getProduct(Model model) {
		model.addAttribute("product", new ProductEntity());
		model.addAttribute("categorias", CategoryEnum.values());

		return "product/add";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String setProduct(@Validated ProductEntity product, BindingResult result, Model model, Principal principal, @RequestParam("images") List<MultipartFile> images)
			throws Exception {
		addOfferValidator.validate(product, result);
		if (result.hasErrors()) {
			model.addAttribute("errors", result.getAllErrors());
			return "product/add";
		}

		product.setProductDate(new Date());
		String email = principal.getName();
		UserEntity user = userService.findByEmail(email);
		product.setUser(user);
		List<String> imageName = imageService.saveImages(images);
		
		product.setImagePaths(imageName);

		productService.addProduct(product);

		return "redirect:/product/own";
	}

	@RequestMapping("/delete/{id}")
	public String deleteProduct(@PathVariable Long id) throws Exception {
		productService.deleteProduct(id);
		return "redirect:/product/own";
	}

	@GetMapping("/edit/{id}")
	public String editProductForm(@PathVariable Long id, Model model) throws Exception {
		ProductEntity product = productService.findById(id);
		model.addAttribute("categorias", CategoryEnum.values());
		model.addAttribute("product", product);
		return "product/editProduct";
	}

	@PostMapping("/edit/{id}")
	public String editProduct(@PathVariable Long id, ProductEntity editedProduct, Model model,
			@RequestParam("images") List<MultipartFile> images) {
		try {

			List<String> imageName = imageService.saveImages(images);

			ProductEntity product = productService.findById(id);
			product.setImagePaths(imageName);
			model.addAttribute("categorias", CategoryEnum.values());

			productService.updateProduct(product, editedProduct);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/product/own";
	}
	 @Value("${image.upload.path}")
	    private String uploadPath;
	
	@RequestMapping("/image/{name}")
	@ResponseBody
	public Resource formatImage(@PathVariable String name) throws IOException {
		
		Path path = Paths.get(uploadPath, name).toAbsolutePath();
		return new ByteArrayResource(Files.readAllBytes(path));
		
	}

	@RequestMapping("/detail")
	public String showProductDetail(@RequestParam Long productId, Model model, Principal principal) throws Exception {
		ProductEntity product = productService.findById(productId);

		if (product == null) {
			throw new NotFoundException("El producto no existe");
		}
		double averageRating = 0.0;
		if (!product.getReviews().isEmpty()) {
			averageRating = productService.calculateAverageRating(productId);

		}
		ReviewEntity review = new ReviewEntity();
		model.addAttribute("review", review);

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
