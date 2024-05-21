CREATE VIEW CUSTOMER_TRANSACTION AS
SELECT
    UUID() AS transaction_id,
    orders.order_date AS transaction_date,
    orders.money_spent AS money_exchanged,
    orders.points_spent AS points_exchanged,
    orders.customer_id AS customer_id,
    orders.points_earned AS points_earned,
    "order" AS transaction_type
FROM orders
UNION ALL
SELECT
    UUID() AS transaction_id,
    refund.refund_date AS transaction_date,
    refund.money_refunded AS money_exchanged,
    refund.points_refunded AS points_exchanged,
    refund.customer_id AS customer_id,
    0 AS points_earned,
    "refund" AS transaction_type
FROM refund;