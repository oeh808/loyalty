package io.brightskies.loyalty.product.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import io.brightskies.loyalty.product.entity.Product;

public interface ProductRepo extends JpaRepository<Product, Integer> {

}
