package com.ecommerce.order.domain.model.entity;

import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

public class OrderModificationHistory {
    
    @NotNull(message = "L'ID de l'historique est obligatoire")
    private Long id;
    
    @NotNull(message = "Le statut précédent est obligatoire")
    private OrderStatus previousStatus;
    
    @NotNull(message = "Le nouveau statut est obligatoire")
    private OrderStatus newStatus;
    
    @NotBlank(message = "La raison de la modification est obligatoire")
    private String reason;
    
    @NotBlank(message = "L'utilisateur qui a effectué la modification est obligatoire")
    private String modifiedBy;
    
    @NotNull(message = "La date de modification est obligatoire")
    private LocalDateTime modifiedAt;
    
    private String additionalNotes;

    public OrderModificationHistory() {
        // Constructeur par défaut pour JPA
    }

    public OrderModificationHistory(OrderStatus previousStatus, OrderStatus newStatus, 
                                  String reason, String modifiedBy) {
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = LocalDateTime.now();
    }

    public OrderModificationHistory(OrderStatus previousStatus, OrderStatus newStatus, 
                                  String reason, String modifiedBy, String additionalNotes) {
        this(previousStatus, newStatus, reason, modifiedBy);
        this.additionalNotes = additionalNotes;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(OrderStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

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

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderModificationHistory that = (OrderModificationHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("OrderModificationHistory{id=%d, %s -> %s, by=%s, at=%s}", 
                id, previousStatus, newStatus, modifiedBy, modifiedAt);
    }
}

