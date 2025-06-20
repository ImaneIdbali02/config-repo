package com.ecommerce.order.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class OrderModifiedByAdmin {
    
    private final Long orderId;
    private final String orderNumber;
    private final String modificationType;
    private final String previousValue;
    private final String newValue;
    private final String adminUser;
    private final String reason;
    private final LocalDateTime occurredAt;

    public OrderModifiedByAdmin(Long orderId, String orderNumber, String modificationType,
                               String previousValue, String newValue, String adminUser, String reason) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.modificationType = modificationType;
        this.previousValue = previousValue;
        this.newValue = newValue;
        this.adminUser = adminUser;
        this.reason = reason;
        this.occurredAt = LocalDateTime.now();
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getModificationType() {
        return modificationType;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderModifiedByAdmin that = (OrderModifiedByAdmin) o;
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
        return String.format("OrderModifiedByAdmin{orderId=%d, orderNumber='%s', type='%s', %s -> %s, by=%s, occurredAt=%s}", 
                orderId, orderNumber, modificationType, previousValue, newValue, adminUser, occurredAt);
    }
}

