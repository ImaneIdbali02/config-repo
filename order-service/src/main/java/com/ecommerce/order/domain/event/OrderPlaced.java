package com.ecommerce.order.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class OrderPlaced {
    
    private final Long orderId;
    private final String orderNumber;
    private final Long customerId;
    private final LocalDateTime occurredAt;

    public OrderPlaced(Long orderId, String orderNumber, Long customerId) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
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

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderPlaced that = (OrderPlaced) o;
        return Objects.equals(orderId, that.orderId) && 
               Objects.equals(orderNumber, that.orderNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderNumber);
    }

    @Override
    public String toString() {
        return String.format("OrderPlaced{orderId=%d, orderNumber='%s', customerId=%d, occurredAt=%s}", 
                orderId, orderNumber, customerId, occurredAt);
    }
}

