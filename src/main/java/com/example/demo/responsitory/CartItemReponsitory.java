package com.example.demo.responsitory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;

@Repository
public interface CartItemReponsitory extends JpaRepository<CartItem, Integer> {

	public List<CartItem> findByCustomer(AppUser customer);

	public CartItem findByCustomerAndProduct(AppUser customer, Product product);

	@Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.product.id= ?2 AND c.customer.id =?3")
	@Modifying
	public void updateQuantity(Integer quantity, Long productId, Long customerId);
	
	@Query("DELETE FROM CartItem c WHERE c.customer.id = ?1 AND c.product.id = ?2")
	@Modifying
	public void deleteByCustomerAndProduct(Long customerId, Long productId);
}
