package io.brightskies.loyalty.product.mapper;

import org.springframework.stereotype.Component;

import io.brightskies.loyalty.product.dto.ProductCreationDto;
import io.brightskies.loyalty.product.dto.ProductUpdatingDto;
import io.brightskies.loyalty.product.entity.Product;

@Component
public class ProductMapper {
    // --- To Entity ---
    public Product toProduct(ProductCreationDto dto) {
        Product product = new Product();
        product.setName(dto.name());
        product.setPrice(dto.price());
        product.setPointsValue(dto.pointsValue());

        return product;
    }

    public Product toProduct(ProductUpdatingDto dto) {
        Product product = new Product();
        product.setName(dto.name());
        product.setPrice(dto.price());
        product.setPointsValue(dto.pointsValue());

        return product;
    }
}
