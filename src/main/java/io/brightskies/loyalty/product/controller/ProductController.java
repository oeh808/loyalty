package io.brightskies.loyalty.product.controller;

import org.springframework.web.bind.annotation.RestController;

import io.brightskies.loyalty.product.dto.ProductCreationDto;
import io.brightskies.loyalty.product.dto.ProductUpdatingDto;
import io.brightskies.loyalty.product.entity.Product;
import io.brightskies.loyalty.product.mapper.ProductMapper;
import io.brightskies.loyalty.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Products", description = "Controller for handling mappings for products")
@RequestMapping("/products")
public class ProductController {
    private ProductService productService;
    private ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @Operation(description = "POST endpoint for creating a product" +
            "\n\n Returns the product created.", summary = "Create a product")
    @PostMapping()
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Must conform to required properties of ProductCreationDto")
    public Product createProduct(@Valid @RequestBody ProductCreationDto dto) {
        Product product = productMapper.toProduct(dto);
        return productService.createProduct(product);
    }

    @Operation(description = "GET endpoint for retrieving a single product given its id" +
            "\n\n Returns an instance of type product.", summary = "Get Single Product")
    @GetMapping("/{id}")
    public Product getProduct(
            @Parameter(in = ParameterIn.PATH, name = "id", description = "Product ID") @PathVariable long id) {
        return productService.getProduct(id);
    }

    @Operation(description = "GET endpoint for retrieving ALL products" +
            "\n\n Returns a list of products.", summary = "Get All Products")
    @GetMapping()
    public List<Product> getAllProducts(
            @Parameter(in = ParameterIn.PATH, name = "id", description = "Product ID") @PathVariable long id) {
        return productService.getAllProducts();
    }

    @Operation(description = "PUT endpoint for updating a product" +
            "\n\n Returns the product updated.", summary = "Update a product")
    @PutMapping("/{id}")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Body is of the type ProductUpdatingDto."
            + "\n \n All fields are optional."
            + " Fields that are left blank are filled by equivalent fields from the original product.")
    public Product updateProduct(
            @Parameter(in = ParameterIn.PATH, name = "id", description = "Product ID") @PathVariable long id,
            @RequestBody ProductUpdatingDto dto) {
        Product product = productMapper.toProduct(dto);
        return productService.updateProduct(id, product);
    }

    @Operation(description = "DELETE endpoint for deleting a single product given its id" +
            "\n\n Returns a String confirming the delete was succcessful.", summary = "Delete Product")
    @DeleteMapping("/{id}")
    public String deleteProduct(
            @Parameter(in = ParameterIn.PATH, name = "id", description = "Product ID") @PathVariable long id) {
        productService.deleteProduct(id);
        return "Product deleted successfully";
    }

}
