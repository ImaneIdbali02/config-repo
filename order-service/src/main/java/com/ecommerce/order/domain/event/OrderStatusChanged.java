package com.ecommerce.order.domain.event;

import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class OrderStatusChanged {
    
    private final Long orderId;
    private final String orderNumber;
    private final OrderStatus previousStatus;
    private final OrderStatus newStatus;
    private final String reason;
    private final String changedBy;
    private final LocalDateTime occurredAt;

    public OrderStatusChanged(Long orderId, String orderNumber, OrderStatus previousStatus, 
                             OrderStatus newStatus, String reason, String changedBy) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.changedBy = changedBy;
        this.occurredAt = LocalDateTime.now();
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public String getReason() {
        return reason;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderStatusChanged that = (OrderStatusChanged) o;
        return Objects.equals(orderId, that.orderId) && 
               Objects.equals(orderNumber, that.orderNumber) &&
               Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderNumber, occurredAt);
    }

    @Override
    public String toString() {
        return String.format("OrderStatusChanged{orderId=%d, orderNumber='%s', %s -> %s, by=%s, occurredAt=%s}", 
                orderId, orderNumber, previousStatus, newStatus, changedBy, occurredAt);
    }
}

