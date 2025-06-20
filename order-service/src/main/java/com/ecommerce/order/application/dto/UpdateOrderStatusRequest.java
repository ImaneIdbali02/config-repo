package com.ecommerce.order.application.dto;

import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusRequest {
    
    @NotNull(message = "Le nouveau statut est obligatoire")
    private OrderStatus newStatus;
    
    @NotBlank(message = "La raison de la modification est obligatoire")
    private String reason;
    
    private String additionalNotes;

    // Constructeurs
    public UpdateOrderStatusRequest() {}

    public UpdateOrderStatusRequest(OrderStatus newStatus, String reason) {
        this.newStatus = newStatus;
        this.reason = reason;
    }

    public UpdateOrderStatusRequest(OrderStatus newStatus, String reason, String additionalNotes) {
        this.newStatus = newStatus;
        this.reason = reason;
        this.additionalNotes = additionalNotes;
    }

    // Getters et Setters
    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(OrderStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    @Override
    public String toString() {
        return String.format("UpdateOrderStatusRequest{newStatus=%s, reason='%s'}", 
                newStatus, reason);
    }
}

