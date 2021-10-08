package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.responsitory.CartItemReponsitory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ShoppingCartTest {

	@Autowired
	private CartItemReponsitory cartRepo;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testAddOneCartITem() {
		Product product = entityManager.find(Product.class, 20L);
		AppUser customer = entityManager.find(AppUser.class, 1L);

		CartItem newItem = new CartItem();
		newItem.setProduct(product);
		newItem.setCustomer(customer);
		newItem.setQuantity(5);

		CartItem savedCartItem = cartRepo.save(newItem);
		assertTrue(savedCartItem.getId() > 0);
	}

	@Test
	public void testGetCartItemByCustomer() {
		AppUser customer = new AppUser();
		customer.setUserId(5L);
		List<CartItem> cartItems = cartRepo.findByCustomer(customer);
		assertEquals(2, cartItems.size());
	}
}
