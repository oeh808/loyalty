package io.brightskies.loyalty.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.brightskies.loyalty.product.entity.Product;
import io.brightskies.loyalty.product.exception.ProductException;
import io.brightskies.loyalty.product.exception.ProductExceptionMessages;
import io.brightskies.loyalty.product.repo.ProductRepo;
import io.brightskies.loyalty.product.service.ProductService;
import io.brightskies.loyalty.product.service.ProductServiceImpl;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class ProductServiceTest {
    @TestConfiguration
    static class ServiceTestConifg {
        @Bean
        @Autowired
        ProductService service(ProductRepo productRepo) {
            return new ProductServiceImpl(productRepo);
        }
    }

    @MockBean
    private ProductRepo productRepo;

    @Autowired
    private ProductService productService;

    private static Product product;

    private static List<Product> products;

    @BeforeAll
    public static void setUp() {
        product = new Product(1, "500g Beef", 300.0f, 20);

        products = new ArrayList<Product>();
        products.add(product);
    }

    @BeforeEach
    public void setUpMocks() {
        when(productRepo.save(product)).thenReturn(product);

        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepo.findById(product.getId() - 1)).thenReturn(Optional.empty());

        when(productRepo.findAll()).thenReturn(products);
    }

    @Test
    public void createProduct_ReturnsSavedProduct() {
        assertEquals(product, productService.createProduct(product));
    }

    @Test
    public void getProduct_RetrievesProductWhenGivenValidId() {
        assertEquals(product, productService.getProduct(product.getId()));
    }

    @Test
    public void getProduct_ThrowsErrorWhenGivenInvalidId() {
        ProductException ex = assertThrows(ProductException.class,
                () -> {
                    productService.getProduct(product.getId() - 1);
                });

        assertTrue(ex.getMessage().contains(ProductExceptionMessages.PRODUCT_NOT_FOUND));
    }

    @Test
    public void getAllProducts_ReturnsAListOfProducts() {
        assertEquals(products, productService.getAllProducts());
    }

    @Test
    public void updateProduct_ReturnsUpdatedProductsWhenGivenValidId() {
        assertEquals(product, productService.updateProduct(product.getId(), product));
    }

    @Test
    public void updateProduct_FillsInBlankFieldsWhenGivenValidIdWhileRetainingNewInformation() {
        Product updatedProduct = new Product(0, null, 250.0f, 0);
        productService.updateProduct(product.getId(), updatedProduct);

        assertEquals(product.getName(), updatedProduct.getName());
        assertNotEquals(product.getPrice(), updatedProduct.getPrice());
    }

    @Test
    public void updateProduct_ThrowsErrorWhenGivenInvalidId() {
        ProductException ex = assertThrows(ProductException.class,
                () -> {
                    productService.updateProduct(product.getId() - 1, product);
                });

        assertTrue(ex.getMessage().contains(ProductExceptionMessages.PRODUCT_NOT_FOUND));

        verify(productRepo, times(0)).save(any(Product.class));
    }

    @Test
    public void deleteProduct_CallsDeleteWhenGivenValidId() {
        productService.deleteProduct(product.getId());

        verify(productRepo, times(1)).deleteById(product.getId());
    }

    @Test
    public void deleteProduct_ThrowsErrorWhenGivenInvalidId() {
        ProductException ex = assertThrows(ProductException.class,
                () -> {
                    productService.deleteProduct(product.getId() - 1);
                });

        assertTrue(ex.getMessage().contains(ProductExceptionMessages.PRODUCT_NOT_FOUND));

        verify(productRepo, times(0)).deleteById(anyLong());
    }
}
