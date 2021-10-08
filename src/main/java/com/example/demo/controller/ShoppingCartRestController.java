package com.example.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.AppUser;
import com.example.demo.service.CartItemService;
import com.example.demo.service.UserService;

@RestController
public class ShoppingCartRestController {

	@Autowired
	private CartItemService cartService;
	@Autowired
	private UserService userService;

	@PostMapping("/cart/add/{pid}/{qty}")
	public String addProductToCart(@PathVariable("pid") Long productId, @PathVariable("qty") Integer quantity,
			Principal pricipal, Model model) {
		AppUser customer = userService.findByUserName(pricipal.getName());
		if (customer == null) {
			return "You must login to add this product to your shopping cart.";
		} else {
			Integer addedQuantity = cartService.addProduct(productId, quantity, customer);
			return addedQuantity + "item(s) of this product were added to your shopping cart.";
		}
	}

	@PostMapping("/cart/update/{pid}/{qty}")
	public String updateQuantity(@PathVariable("pid") Long productId, @PathVariable("qty") Integer quantity,
			Principal pricipal, Model model) {
		AppUser customer = userService.findByUserName(pricipal.getName());
		if (customer == null) {
			return "You must login to update this product to your shopping cart.";
		} else {
			double subtotal = cartService.updateQuantity(productId, quantity, customer);
			return String.valueOf(subtotal);
		}
	}

//	@PostMapping("/cart/remove/{pid}")
//	public String removeProductFromCart(@PathVariable("pid") Long productId, @PathVariable("qty") Integer quantity,
//			Principal pricipal, Model model) {
//		AppUser customer = userService.findByUserName(pricipal.getName());
//		System.out.println("remove cart");
//		if (customer == null) {
//			return "You must login to remove this product to your shopping cart.";
//		} else {
//
//			cartService.removeCartItem(productId, customer);
//		}
//		return "The product has been removed from your shopping cart.";
//	}
}
