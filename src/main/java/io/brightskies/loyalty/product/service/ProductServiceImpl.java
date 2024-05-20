package io.brightskies.loyalty.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.brightskies.loyalty.product.entity.Product;
import io.brightskies.loyalty.product.exception.ProductException;
import io.brightskies.loyalty.product.exception.ProductExceptionMessages;
import io.brightskies.loyalty.product.repo.ProductRepo;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ProductServiceImpl implements ProductService {
    private ProductRepo productRepo;

    public ProductServiceImpl(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public Product createProduct(Product product) {
        log.info("Running createProduct(" + product.toString() + ") in ProductServiceImpl...");
        return productRepo.save(product);
    }

    @Override
    public Product getProduct(long id) {
        log.info("Running getProduct(" + id + ") in ProductServiceImpl...");
        Optional<Product> opProduct = productRepo.findById(id);

        if (opProduct.isPresent()) {
            return opProduct.get();
        } else {
            log.error("Invalid product id: " + id + "!");
            throw new ProductException(ProductExceptionMessages.PRODUCT_NOT_FOUND);
        }
    }

    @Override
    public List<Product> getAllProducts() {
        log.info("Running getAllProducts() in ProductServiceImpl...");
        return productRepo.findAll();
    }

    @Override
    public Product updateProduct(long id, Product product) {
        log.info("Running updateProduct(" + id + ", " + product.toString() + ") in ProductServiceImpl...");
        log.info("Checking product id exists...");
        Optional<Product> opProduct = productRepo.findById(id);

        if (opProduct.isEmpty()) {
            // Only products that already exist can be updated
            log.error("Invalid product id: " + id + "!");
            throw new ProductException(ProductExceptionMessages.PRODUCT_NOT_FOUND);
        } else {
            log.info("Updating product...");
            product.setId(id);
            Product originalProduct = opProduct.get();
            product = mergeProducts(product, originalProduct);

            log.info("Saving updated product...");
            return productRepo.save(product);
        }
    }

    @Override
    public void deleteProduct(long id) {
        log.info("Running deleteProduct(" + id + ") in ProductServiceImpl...");
        log.info("Checking product id exists...");
        Optional<Product> opProduct = productRepo.findById(id);

        if (opProduct.isEmpty()) {
            // Only products that already exist can be deleted
            log.error("Invalid product id: " + id + "!");
            throw new ProductException(ProductExceptionMessages.PRODUCT_NOT_FOUND);
        } else {
            log.info("Deleting product...");
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
        log.info("Running updateProduct(" + updatedProduct.toString() + ", " + originalProduct.toString()
                + ") in ProductServiceImpl...");

        if (updatedProduct.getName() == null || updatedProduct.getName() == "") {
            log.info("Updating product name...");
            updatedProduct.setName(originalProduct.getName());
        }
        if (updatedProduct.getPrice() <= 0) {
            log.info("Updating product price...");
            updatedProduct.setPrice(originalProduct.getPrice());
        }
        if (updatedProduct.getPointsValue() <= 0) {
            log.info("Updating product points value...");
            updatedProduct.setPointsValue(originalProduct.getPointsValue());
        }

        return updatedProduct;
    }

}
