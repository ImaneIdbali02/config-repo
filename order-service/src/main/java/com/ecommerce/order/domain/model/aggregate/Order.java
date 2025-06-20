package com.ecommerce.order.domain.model.aggregate;

import com.ecommerce.order.domain.model.entity.OrderLine;
import com.ecommerce.order.domain.model.entity.OrderModificationHistory;
import com.ecommerce.order.domain.model.valueobject.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {
    
    @NotNull(message = "L'ID de la commande est obligatoire")
    private Long id;
    
    @NotBlank(message = "Le numéro de commande est obligatoire")
    private String orderNumber;
    
    @NotNull(message = "L'ID du client est obligatoire")
    private Long customerId;
    
    @NotNull(message = "Le statut de la commande est obligatoire")
    private OrderStatus status;
    
    @NotNull(message = "L'adresse de livraison est obligatoire")
    private DeliveryAddress deliveryAddress;
    
    @NotNull(message = "L'adresse de facturation est obligatoire")
    private BillingAddress billingAddress;
    
    @NotNull(message = "Le résumé de paiement est obligatoire")
    private PaymentSummary paymentSummary;
    
    @NotNull(message = "La date de création est obligatoire")
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    
    private String customerNotes;
    private String internalNotes;
    
    @NotNull(message = "Les lignes de commande sont obligatoires")
    private List<OrderLine> orderLines = new ArrayList<>();
    
    private List<OrderModificationHistory> modificationHistory = new ArrayList<>();

    public Order() {
        // Constructeur par défaut pour JPA
    }

    public Order(String orderNumber, Long customerId, DeliveryAddress deliveryAddress, 
                BillingAddress billingAddress, List<OrderLine> orderLines) {
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.status = OrderStatus.PENDING;
        this.deliveryAddress = deliveryAddress;
        this.billingAddress = billingAddress;
        this.orderLines = new ArrayList<>(orderLines);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.paymentSummary = calculatePaymentSummary();
    }

    // Méthodes métier
    public void confirmOrder(String confirmedBy) {
        if (!status.canTransitionTo(OrderStatus.CONFIRMED)) {
            throw new IllegalStateException("Impossible de confirmer la commande dans l'état actuel: " + status);
        }
        
        OrderStatus previousStatus = this.status;
        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        addModificationHistory(previousStatus, OrderStatus.CONFIRMED, 
                             "Commande confirmée", confirmedBy);
    }

    public void updateStatus(OrderStatus newStatus, String reason, String modifiedBy) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Transition de statut invalide: %s -> %s", status, newStatus));
        }
        
        OrderStatus previousStatus = this.status;
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
        
        // Mettre à jour les timestamps spécifiques
        switch (newStatus) {
            case SHIPPED -> this.shippedAt = LocalDateTime.now();
            case DELIVERED -> this.deliveredAt = LocalDateTime.now();
        }
        
        addModificationHistory(previousStatus, newStatus, reason, modifiedBy);
    }

    public void addOrderLine(OrderLine orderLine) {
        this.orderLines.add(orderLine);
        this.paymentSummary = calculatePaymentSummary();
        this.updatedAt = LocalDateTime.now();
    }

    public void removeOrderLine(Long orderLineId) {
        this.orderLines.removeIf(line -> Objects.equals(line.getId(), orderLineId));
        this.paymentSummary = calculatePaymentSummary();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateOrderLineQuantity(Long orderLineId, Integer newQuantity) {
        OrderLine orderLine = orderLines.stream()
                .filter(line -> Objects.equals(line.getId(), orderLineId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ligne de commande non trouvée: " + orderLineId));
        
        orderLine.updateQuantity(newQuantity);
        this.paymentSummary = calculatePaymentSummary();
        this.updatedAt = LocalDateTime.now();
    }

    private PaymentSummary calculatePaymentSummary() {
        BigDecimal totalAmount = orderLines.stream()
                .map(OrderLine::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal taxAmount = totalAmount.multiply(BigDecimal.valueOf(0.20)); // 20% TVA
        BigDecimal shippingAmount = BigDecimal.valueOf(5.99); // Frais de port fixes
        BigDecimal discountAmount = BigDecimal.ZERO; // Pas de remise par défaut
        
        return new PaymentSummary(totalAmount, taxAmount, shippingAmount, discountAmount, "EUR");
    }

    private void addModificationHistory(OrderStatus previousStatus, OrderStatus newStatus, 
                                      String reason, String modifiedBy) {
        OrderModificationHistory history = new OrderModificationHistory(
                previousStatus, newStatus, reason, modifiedBy);
        this.modificationHistory.add(history);
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    public boolean isDelivered() {
        return status == OrderStatus.DELIVERED;
    }

    public boolean isPaid() {
        return status == OrderStatus.PAID || 
               status == OrderStatus.PREPARING || 
               status == OrderStatus.READY_FOR_SHIPMENT ||
               status == OrderStatus.SHIPPED || 
               status == OrderStatus.DELIVERED;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public BillingAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(BillingAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public PaymentSummary getPaymentSummary() {
        return paymentSummary;
    }

    public void setPaymentSummary(PaymentSummary paymentSummary) {
        this.paymentSummary = paymentSummary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getCustomerNotes() {
        return customerNotes;
    }

    public void setCustomerNotes(String customerNotes) {
        this.customerNotes = customerNotes;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public List<OrderLine> getOrderLines() {
        return Collections.unmodifiableList(orderLines);
    }

    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = new ArrayList<>(orderLines);
    }

    public List<OrderModificationHistory> getModificationHistory() {
        return Collections.unmodifiableList(modificationHistory);
    }

    public void setModificationHistory(List<OrderModificationHistory> modificationHistory) {
        this.modificationHistory = new ArrayList<>(modificationHistory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(orderNumber, order.orderNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderNumber);
    }

    @Override
    public String toString() {
        return String.format("Order{id=%d, orderNumber='%s', status=%s, customerId=%d}", 
                id, orderNumber, status, customerId);
    }
}

