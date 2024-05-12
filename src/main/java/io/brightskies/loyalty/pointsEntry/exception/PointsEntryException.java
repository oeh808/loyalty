package io.brightskies.loyalty.pointsEntry.exception;

public class PointsEntryException extends RuntimeException {
    private String message;

    public PointsEntryException(String message) {
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
