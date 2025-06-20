package com.ecommerce.order.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class OrderCancelled {
    
    private final Long orderId;
    private final String orderNumber;
    private final Long customerId;
    private final String reason;
    private final String cancelledBy;
    private final LocalDateTime occurredAt;

    public OrderCancelled(Long orderId, String orderNumber, Long customerId, 
                         String reason, String cancelledBy) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.reason = reason;
        this.cancelledBy = cancelledBy;
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

    public String getReason() {
        return reason;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderCancelled that = (OrderCancelled) o;
        return Objects.equals(orderId, that.orderId) && 
               Objects.equals(orderNumber, that.orderNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderNumber);
    }

    @Override
    public String toString() {
        return String.format("OrderCancelled{orderId=%d, orderNumber='%s', reason='%s', cancelledBy='%s', occurredAt=%s}", 
                orderId, orderNumber, reason, cancelledBy, occurredAt);
    }
}

