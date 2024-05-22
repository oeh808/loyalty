package io.brightskies.loyalty.order.exception;

public class OrderExceptionMessages {
    public static String ORDER_NOT_FOUND = "An order with the given id does not exist";
    public static String NOT_ENOUGH_POINTS = "The customer does not have enough points to make this purchase with the provided points";
    public static String PRODUCT_NOT_FOUND_IN_ORDER = "The product with the given id does not exist in the order";

    public  static String ORDER_ALREADY_REFUNDED = "The order has already been refunded";
}
