package com.tfg.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.entity.ProductEntity;
import com.tfg.entity.ReviewEntity;
import com.tfg.entity.UserEntity;
import com.tfg.entity.enums.CategoryEnum;
import com.tfg.exception.NotFoundException;
import com.tfg.exception.UpdateProductException;
import com.tfg.service.ImageService;
import com.tfg.service.ProductService;
import com.tfg.service.RecommendationSystemService;
import com.tfg.service.ShoppingCartService;
import com.tfg.service.UserService;

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
	private RecommendationSystemService recommendedService;

	@Autowired
	private ShoppingCartService cartService;

	public ProductController(ProductService productService, UserService userService,
			RecommendationSystemService recommendedService, ShoppingCartService cartService) {
		super();
		this.productService = productService;
		this.userService = userService;

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

		List<ProductEntity> recommended;
		Set<ProductEntity> filteredProducts;

		List<ProductEntity> popular = recommendedService.getMostPopularProducts();
		List<ProductEntity> topRated = recommendedService.getTopRatedProducts();

		Set<ProductEntity> allRecommendedProducts = new HashSet<>();

		allRecommendedProducts.addAll(popular);
		allRecommendedProducts.addAll(topRated);

		if (principal == null) {
			recommended = new ArrayList<>();
			filteredProducts = allRecommendedProducts;
		} else {
			String email = principal.getName();
			UserEntity user = userService.findByEmail(email);

			recommended = recommendedService.getRecommendedProducts(user.getUserId());

			filteredProducts = allRecommendedProducts.stream()
					.filter(product -> product.getStock() > 0 && !product.getUser().equals(user))
					.collect(Collectors.toSet());
		}

		allRecommendedProducts.addAll(recommended);

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
			@RequestParam("images") List<MultipartFile> images, @RequestParam("tags") String tags) throws Exception {

		if (result.hasErrors()) {
			model.addAttribute("errors", result.getAllErrors());
			return "product/add";
		}

		List<String> tagList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode tagsJson = mapper.readTree(tags);
		for (JsonNode tag : tagsJson) {
			System.out.println(tag);
			System.out.println(tag.get("value"));
			tagList.add(tag.get("value").asText());
		}

		product.setTags(tagList);

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
			@RequestParam(name = "imagesToRemove", required = false) List<String> imagesToRemove,
			@RequestParam(name = "tags") String tags) throws Exception {
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
				for (String i : imageNames) {
					editedProduct.addImagePath(i);
					product.addImagePath(i);
				}
				model.addAttribute("categorias", CategoryEnum.values());

			} else {
				editedProduct.setImagePaths(product.getImagePaths());
			}
			editedProduct.setProductId(product.getProductId());
			List<String> tagList = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tagsJson = mapper.readTree(tags);
			for (JsonNode tag : tagsJson) {
				System.out.println(tag);
				System.out.println(tag.get("value"));
				tagList.add(tag.get("value").asText());
			}
			editedProduct.setTags(tagList);
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

			List<ProductEntity> similarProducts = recommendedService.findSimilarProductsByTags(productId);
			model.addAttribute("similarProducts", similarProducts);

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
	
	@GetMapping("/category/{category}")
    public String showProductsByCategory(@PathVariable("category") String category, Model model) {
		CategoryEnum categoryEnum = CategoryEnum.valueOf(category.toUpperCase()); 
		   
        List<ProductEntity> products = productService.getProductsByCategory(categoryEnum);
        
        model.addAttribute("searchResults", products);
       model.addAttribute("searchTerm", category);
        
        return "product/searchResults"; 
    }


	@ModelAttribute
	public void loadCurrentUser(Model model, Principal p) throws Exception {
		if (p != null) {
			model.addAttribute("currentUser", userService.findByEmail(p.getName()));
		} else {
			model.addAttribute("currentUser", null);
		}
	}

	@ModelAttribute
	public void loadCart(Model model, Principal p) throws Exception {
		model.addAttribute("cart", cartService.getCart());
	}
}
