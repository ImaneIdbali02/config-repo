package com.ecommerce.order.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class OrderReadyForShipment {
    
    private final Long orderId;
    private final String orderNumber;
    private final Long customerId;
    private final String preparedBy;
    private final LocalDateTime occurredAt;

    public OrderReadyForShipment(Long orderId, String orderNumber, Long customerId, String preparedBy) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.preparedBy = preparedBy;
        this.occurredAt = LocalDateTime.now();
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getPreparedBy() {
        return preparedBy;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderReadyForShipment that = (OrderReadyForShipment) o;
        return Objects.equals(orderId, that.orderId) && 
               Objects.equals(orderNumber, that.orderNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderNumber);
    }

    @Override
    public String toString() {
        return String.format("OrderReadyForShipment{orderId=%d, orderNumber='%s', customerId=%d, preparedBy='%s', occurredAt=%s}", 
                orderId, orderNumber, customerId, preparedBy, occurredAt);
    }
}

