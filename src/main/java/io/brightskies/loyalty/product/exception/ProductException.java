package io.brightskies.loyalty.product.exception;

public class ProductException extends RuntimeException {
    private String message;

    public ProductException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
