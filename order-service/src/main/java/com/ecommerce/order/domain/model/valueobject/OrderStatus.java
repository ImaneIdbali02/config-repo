package com.ecommerce.order.domain.model.valueobject;

public enum OrderStatus {
    PENDING("En attente"),
    CONFIRMED("Confirmée"),
    PAID("Payée"),
    PREPARING("En préparation"),
    READY_FOR_SHIPMENT("Prête pour expédition"),
    SHIPPED("Expédiée"),
    DELIVERED("Livrée"),
    CANCELLED("Annulée"),
    REFUNDED("Remboursée");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED -> newStatus == PAID || newStatus == CANCELLED;
            case PAID -> newStatus == PREPARING || newStatus == CANCELLED;
            case PREPARING -> newStatus == READY_FOR_SHIPMENT || newStatus == CANCELLED;
            case READY_FOR_SHIPMENT -> newStatus == SHIPPED;
            case SHIPPED -> newStatus == DELIVERED;
            case DELIVERED -> newStatus == REFUNDED;
            case CANCELLED, REFUNDED -> false;
        };
    }
}

