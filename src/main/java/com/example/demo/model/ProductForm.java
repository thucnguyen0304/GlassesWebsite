package com.example.demo.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ProductForm {
    private Long id;
    private String name;
    private double price;
    private String description;
    private MultipartFile image;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public MultipartFile getImage() {
		return image;
	}
	public void setImage(MultipartFile image) {
		this.image = image;
	}

    public ProductForm() {
    }
	public ProductForm(String name, double price, String description, MultipartFile image) {
		super();
		this.name = name;
		this.price = price;
		this.description = description;
		this.image = image;
	}
    
//    public ProductForm(ProductFormBuilder productFormBuilder) {
//        this.name = productFormBuilder.name;
//        this.description = productFormBuilder.description;
//        this.image = productFormBuilder.image;
//        this.price=productFormBuilder.price;
//    }
//
//    public static class ProductFormBuilder {
//        private final String name;
//        private double price;
//        private String description;
//        private byte[] image;
//
//        public ProductFormBuilder(String name) {
//            this.name = name;
//        }
//
//        public ProductFormBuilder description(String description) {
//            this.description = description;
//            return this;
//        }
//
//        public ProductFormBuilder image(byte[] image) {
//            this.image = image;
//            return this;
//        }
//        public ProductFormBuilder price(double price) {
//            this.price = price;
//            return this;
//        }
//
//        public ProductForm build() {
//            return new ProductForm(this);
//        }
//    }
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	}
//
//	public byte[] getImage() {
//		return image;
//	}
//
//	public void setImage(byte[] image) {
//		this.image = image;
//	}
//
//	public double getPrice() {
//		return price;
//	}
//
//	public void setPrice(double price) {
//		this.price = price;
//	}
}
