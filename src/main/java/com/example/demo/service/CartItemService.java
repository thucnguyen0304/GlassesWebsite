package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.responsitory.CartItemReponsitory;
import com.example.demo.responsitory.ProductResponsitory;

@Service
@Transactional
public class CartItemService {

	@Autowired
	private CartItemReponsitory cartRepo;

	@Autowired
	private ProductResponsitory productRepo;

	public List<CartItem> listCartItems(AppUser customer) {
		return cartRepo.findByCustomer(customer);
	}

	public Integer addProduct(Long productId, int quantity, AppUser customer) {

		Integer addedQuantity = quantity;

		Optional<Product> productOptional = productRepo.findById(productId);
		Product product = null;
		if (productOptional.isPresent()) {
			product = productOptional.get();
		}
		CartItem cartItem = cartRepo.findByCustomerAndProduct(customer, product);

		if (cartItem != null) {
			addedQuantity = cartItem.getQuantity() + quantity;
			cartItem.setQuantity(addedQuantity);
		} else {
			cartItem = new CartItem();
			cartItem.setQuantity(quantity);
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);
		}

		cartRepo.save(cartItem);
		return addedQuantity;
	}

	public double updateQuantity(Long productId, Integer quantity, AppUser customer) {
		cartRepo.updateQuantity(quantity, productId, customer.getUserId());
		Product product = productRepo.findById(productId).get();

		double subtotal = product.getPrice() * quantity;
		return subtotal;
	}

	public void removeCartItem(Long productId, AppUser customer) {
		cartRepo.deleteByCustomerAndProduct(customer.getUserId(), productId);
	}

	public void deleteCart(Integer id) {
		cartRepo.deleteById(id);
	}
}
