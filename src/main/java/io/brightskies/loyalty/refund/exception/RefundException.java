package io.brightskies.loyalty.refund.exception;

public class RefundException extends RuntimeException {
    private String message;

    public RefundException(String message) {
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
