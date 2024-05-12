package io.brightskies.loyalty.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.brightskies.loyalty.product.entity.Product;
import io.brightskies.loyalty.product.exception.ProductException;
import io.brightskies.loyalty.product.exception.ProductExceptionMessages;
import io.brightskies.loyalty.product.repo.ProductRepo;

@Service
public class ProductServiceImpl implements ProductService {
    private ProductRepo productRepo;

    public ProductServiceImpl(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public Product createProduct(Product product) {
        return productRepo.save(product);
    }

    @Override
    public Product getProduct(long id) {
        Optional<Product> opProduct = productRepo.findById(id);

        if (opProduct.isPresent()) {
            return opProduct.get();
        } else {
            throw new ProductException(ProductExceptionMessages.PRODUCT_NOT_FOUND);
        }
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public Product updateProduct(long id, Product product) {
        Optional<Product> opProduct = productRepo.findById(id);

        if (opProduct.isEmpty()) {
            // Only products that already exist can be updated
            throw new ProductException(ProductExceptionMessages.PRODUCT_NOT_FOUND);
        } else {
            product.setId(id);
            Product originalProduct = opProduct.get();
            product = mergeProducts(product, originalProduct);

            return product;
        }
    }

    @Override
    public void deleteProduct(long id) {
        Optional<Product> opProduct = productRepo.findById(id);

        if (opProduct.isEmpty()) {
            // Only products that already exist can be deleted
            throw new ProductException(ProductExceptionMessages.PRODUCT_NOT_FOUND);
        } else {
            productRepo.deleteById(id);
        }
    }

    // Helper functions
    // ________________
    /*
     * The purpose of this function is to fill in fields that have not been sent in
     * an update request to allow users to only need to provide values for the
     * fields they want to update.
     */
    private Product mergeProducts(Product updatedProduct, Product originalProduct) {
        if (updatedProduct.getName() == null) {
            updatedProduct.setName(originalProduct.getName());
        }
        if (updatedProduct.getPrice() <= 0) {
            updatedProduct.setPrice(originalProduct.getPrice());
        }
        if (updatedProduct.getPointsValue() <= 0) {
            updatedProduct.setPointsValue(originalProduct.getPointsValue());
        }

        return updatedProduct;
    }

}
