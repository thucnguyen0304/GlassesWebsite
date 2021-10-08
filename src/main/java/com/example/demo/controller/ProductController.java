package com.example.demo.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.model.ProductForm;
import com.example.demo.service.CartItemService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;

@Controller
public class ProductController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartItemService cartService;

	@Value("${uploadDir}")
	private String uploadFolder;

	@Autowired
	ProductService productService;

	@GetMapping(value = { "/new_product" })
	public String addProductPage(Model model, Principal principal) {
		showShoppingCart(model, principal);
		return "new_product";
	}

	@PostMapping("/new_product_process")
	public @ResponseBody ResponseEntity<?> createProduct(@RequestParam("name") String name,
			@RequestParam("price") double price, @RequestParam("description") String description, Model model,
			HttpServletRequest request, final @RequestParam("image") MultipartFile file) {
		try {
			String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);
			String fileName = file.getOriginalFilename();
			String filePath = Paths.get(uploadDirectory, fileName).toString();
			if (fileName == null || fileName.contains("..")) {
				model.addAttribute("invalid", "Sorry! Filename contains invalid path sequence \" + fileName");
				return new ResponseEntity<>("Sorry! Filename contains invalid path sequence " + fileName,
						HttpStatus.BAD_REQUEST);
			}
			String[] names = name.split(",");
			String[] descriptions = description.split(",");
			Date createDate = new Date();
			try {
				File dir = new File(uploadDirectory);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
				stream.write(file.getBytes());
				stream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			byte[] imageData = file.getBytes();
			Product product = new Product();
			product.setName(names[0]);
			product.setImage(imageData);
			product.setPrice(price);
			product.setDescription(descriptions[0]);
			product.setCreateDate(createDate);
			productService.saveProduct(product);
			return new ResponseEntity<>("Product Saved With File - " + fileName, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/image/display/{id}")
	@ResponseBody
	void showImage(@PathVariable("id") Long id, HttpServletResponse response, Optional<Product> product)
			throws ServletException, IOException {
		product = productService.getProductById(id);
		response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
		response.getOutputStream().write(product.get().getImage());
		response.getOutputStream().close();
	}

	@GetMapping("/single")
	String showProductDetails(@RequestParam("id") Long id, Optional<Product> product, Model model,
			Principal principal) {
		showShoppingCart(model, principal);
		try {
			if (id != 0) {
				product = productService.getProductById(id);

				if (product.isPresent()) {
					model.addAttribute("id", product.get().getId());
					model.addAttribute("description", product.get().getDescription());
					model.addAttribute("name", product.get().getName());
					model.addAttribute("price", product.get().getPrice());
					return "single";
				}
				return "redirect:/checkout";
			}
			return "redirect:/home";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/home";
		}
	}

	@GetMapping("/admin")
	String show(Model map, Principal principal) {
		List<Product> images = productService.listProduct();
		map.addAttribute("images", images);
		showShoppingCart(map, principal);
		return "adminPage";
	}

	@PostMapping("/edit_product")
	public String editProduct(@ModelAttribute("product") ProductForm productForm, Model model,
			HttpServletRequest request, Principal principal) {
		Optional<Product> product = productService.getProductById(productForm.getId());
		Product productEntity = product.get();
		showShoppingCart(model, principal);
		try {
			Date createDate = new Date();

			byte[] imageData = productForm.getImage().getBytes();
			model.addAttribute("imageModel", productEntity);
			productEntity.setName(productForm.getName());
			if (productForm.getImage().isEmpty()) {
				productEntity.setImage(productEntity.getImage());
			} else {
				productEntity.setImage(imageData);
			}
			productEntity.setPrice(productForm.getPrice());
			productEntity.setDescription(productForm.getDescription());
			productEntity.setCreateDate(createDate);
			productService.saveProduct(productEntity);
			return "redirect:/admin";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/edit_product/" + productForm.getId();
		}
	}

	@GetMapping("/edit_product/{id}")
	public ModelAndView showEditProductPage(@PathVariable(name = "id") Long id, Optional<Product> product, Model model,
			Principal principal) {
		ModelAndView mav;
		showShoppingCart(model, principal);
		product = productService.getProductById(id);
		if (!product.isPresent()) {
			mav = new ModelAndView("404");
			return mav;
		} else {
			mav = new ModelAndView("edit_product");
			mav.addObject("product", product);
			return mav;
		}

	}

	@GetMapping("/delete")
	String deleteProduct(@RequestParam("id") Long id) {
		productService.deleteProductById(id);
		return "redirect:/admin";
	}

	@RequestMapping("/search")
	public String searchPage(Model model, @Param("keyword") String keyword) {
		List<Product> listProducts = productService.search(keyword);
		List<List<Product>> subProducts = ListUtils.partition(listProducts, 3);
		model.addAttribute("listProducts", subProducts);
		model.addAttribute("keyword", keyword);

		return "search";
	}

	public String showShoppingCart(Model model, Principal principal) {
		if (principal != null) {
			AppUser customer = userService.findByUserName(principal.getName());
			List<CartItem> cartItems = cartService.listCartItems(customer);
			model.addAttribute("cartItems", cartItems);
		} else {
			return "loginPage";
		}
		return null;
	}
}
