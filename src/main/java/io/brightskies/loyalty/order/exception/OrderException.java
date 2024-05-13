package io.brightskies.loyalty.order.exception;

public class OrderException extends RuntimeException {
    private String message;

    public OrderException(String message) {
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
