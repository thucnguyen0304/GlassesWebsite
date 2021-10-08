package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Product;
import com.example.demo.responsitory.ProductResponsitory;

@Service
public class ProductService {

	@Autowired
	private ProductResponsitory productRepo;

	public void saveProduct(Product product) {
		productRepo.save(product);
	}

	public List<Product> listProduct() {
		return productRepo.findAll();
	}

	public Optional<Product> getProductById(Long id) {
		return productRepo.findById(id);
	}

	public void deleteProductById(Long id) {
		productRepo.deleteById(id);
		;
	}

	public List<Product> search(String keyword) {
		if (keyword != null) {
			return productRepo.search(keyword);
		}
		return productRepo.findAll();
	}
}
