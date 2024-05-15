package io.brightskies.loyalty.product.controller;

import org.springframework.web.bind.annotation.RestController;

import io.brightskies.loyalty.product.dto.ProductCreationDto;
import io.brightskies.loyalty.product.dto.ProductUpdatingDto;
import io.brightskies.loyalty.product.entity.Product;
import io.brightskies.loyalty.product.mapper.ProductMapper;
import io.brightskies.loyalty.product.service.ProductService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/products")
public class ProductController {
    private ProductService productService;
    private ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @PostMapping()
    public Product createProduct(@Valid @RequestBody ProductCreationDto dto) {
        Product product = productMapper.toProduct(dto);
        return productService.createProduct(product);
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable long id) {
        return productService.getProduct(id);
    }

    @GetMapping()
    public List<Product> getAllProducts(@PathVariable long id) {
        return productService.getAllProducts();
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable long id, @RequestBody ProductUpdatingDto dto) {
        Product product = productMapper.toProduct(dto);
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
        return "Product deleted successfully";
    }

}
