package io.brightskies.loyalty.transaction.exception;

public class TransactionException extends RuntimeException {
    private String message;

    public TransactionException(String message) {
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
