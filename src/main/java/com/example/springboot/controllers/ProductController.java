package com.example.springboot.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {

	@Autowired
	ProductRepository productRepository;

	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts() {
		List<ProductModel> products = productRepository.findAll();
		if (!products.isEmpty()) {
			for (ProductModel product : products) {
				product.add(
						linkTo(methodOn(ProductController.class).getOneProduct(product.getIdProduct())).withSelfRel());
			}

		}
		return ResponseEntity.status(HttpStatus.OK).body(products);
	}

	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id) {
		Optional<ProductModel> product = productRepository.findById(id);
		if (product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(product.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("list_products")));
	}

	@PostMapping("/products")
	public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {
		var product = new ProductModel();
		BeanUtils.copyProperties(productRecordDto, product);
		return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(product));
	}

	@PutMapping("/products/{id}")
	public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
			@RequestBody @Valid ProductRecordDto productRecordDto) {
		Optional<ProductModel> product = productRepository.findById(id);
		if (product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
		}
		var productModel = product.get();
		BeanUtils.copyProperties(productRecordDto, productModel);
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
	}

	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
		Optional<ProductModel> product = productRepository.findById(id);
		if (product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
		}
		productRepository.delete(product.get());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}
}
