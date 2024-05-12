package io.brightskies.loyalty.product.service;

import java.util.List;

import io.brightskies.loyalty.product.entity.Product;

public interface ProductService {
    Product createProduct(Product product);

    Product getProduct(long id);

    List<Product> getAllProducts();

    Product updateProduct(long id, Product product);

    void deleteProduct(long id);
}
