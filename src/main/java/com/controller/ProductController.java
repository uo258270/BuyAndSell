package com.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.entity.ProductEntity;
import com.entity.ReviewEntity;
import com.entity.UserEntity;
import com.entity.enums.CategoryEnum;
import com.exception.NotFoundException;
import com.exception.UpdateProductException;
import com.service.ImageService;
import com.service.ProductService;
import com.service.RecommendationSystemService;
import com.service.ShoppingCartService;
import com.service.UserService;
import com.validators.AddOfferValidator;

@Controller
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private UserService userService;

	@Autowired
	private AddOfferValidator addOfferValidator;

	@Autowired
	private RecommendationSystemService recommendedService;
	
	@Autowired
	private ShoppingCartService cartService;

	public ProductController(ProductService productService, UserService userService,
			AddOfferValidator addOfferValidator, RecommendationSystemService recommendedService, ShoppingCartService cartService) {
		super();
		this.productService = productService;
		this.userService = userService;
		this.addOfferValidator = addOfferValidator;
		this.recommendedService = recommendedService;
		this.cartService = cartService;
	}

	@GetMapping("/own")
	public String listOwnProducts(Model model, Principal principal) throws Exception {
		if (principal == null) {
			return "redirect:/login";
		}
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

	    List<ProductEntity> recommended = recommendedService.getRecommendedProducts(user.getUserId());
	    List<ProductEntity> popular = recommendedService.getMostPopularProducts();
	    List<ProductEntity> topRated = recommendedService.getTopRatedProducts();

	    Set<ProductEntity> allRecommendedProducts = new HashSet<>();
	    allRecommendedProducts.addAll(recommended);
	    allRecommendedProducts.addAll(popular);
	    allRecommendedProducts.addAll(topRated);

	    Set<ProductEntity> filteredProducts = allRecommendedProducts.stream()
	            .filter(product -> product.getStock() > 0 && !product.getUser().equals(user))
	            .collect(Collectors.toSet());

	    model.addAttribute("products", filteredProducts);
	    model.addAttribute("recommended", recommended);
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
	public String setProduct(@Validated ProductEntity product, BindingResult result, Model model, Principal principal,
			@RequestParam("images") List<MultipartFile> images) throws Exception {
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
			@RequestParam(name = "images", required = false) List<MultipartFile> images,
			@RequestParam(name = "imagesToRemove", required = false) List<String> imagesToRemove) throws Exception {
		try {
			ProductEntity product = productService.findById(id);
			List<MultipartFile> nonEmptyImages = images.stream().filter(image -> !image.isEmpty())
					.collect(Collectors.toList());

			if (imagesToRemove != null && !imagesToRemove.isEmpty()) {
				imageService.deleteImages(imagesToRemove);
				product.getImagePaths().removeAll(imagesToRemove);
				editedProduct.getImagePaths().removeAll(imagesToRemove);
			}
			if (nonEmptyImages != null && !nonEmptyImages.isEmpty()) {
				List<String> imageNames = imageService.saveImages(images);
				for(String i : imageNames) {
					editedProduct.addImagePath(i);
					product.addImagePath(i);
				}
				model.addAttribute("categorias", CategoryEnum.values());

			} else {
				editedProduct.setImagePaths(product.getImagePaths());
			}
			editedProduct.setProductId(product.getProductId());
			productService.updateProduct(product, editedProduct);

		} catch (NotFoundException e) {
			throw new NotFoundException("El producto no se ha encontrado en el servicio");
		} catch (UpdateProductException e) {
			throw new UpdateProductException("Error al actualizar el producto");
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

	@RequestMapping(value = "/detail/{productId}")
	public String showProductDetail(@PathVariable Long productId, Model model, Principal principal) throws Exception {
		try {
			ProductEntity product = productService.findById(productId);
			double averageRating = 0.0;
			if (!product.getReviews().isEmpty()) {
				averageRating = productService.calculateAverageRating(productId);

			}
			ReviewEntity review = new ReviewEntity();
			model.addAttribute("review", review);

			productService.increaseNumOfViews(product);

			model.addAttribute("product", product);
			model.addAttribute("principal", principal);
			model.addAttribute("averageRating", averageRating);

			return "product/detail";
		} catch (NotFoundException e) {
			throw new NotFoundException("El producto no existe");
		}
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

	@GetMapping("/purchased")
	public String listPurchasedProducts(Model model, Principal principal) throws Exception {
		String email = principal.getName();
		UserEntity user = userService.findByEmail(email);
		List<ProductEntity> purchasedProducts = productService.getPurchasedProducts(user.getUserId());
		model.addAttribute("purchasedProducts", purchasedProducts);
		return "product/listPurchased";
	}
	
	@ModelAttribute
	public void loadCurrentUser(Model model, Principal p) throws Exception {
		if (p != null) {
			model.addAttribute("currentUser", userService.findByEmail(p.getName()));
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
