package com.ecommerce.order.infrastructure.persistence.entity;

import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    @Column(length = 1000)
    private String customerNotes;

    @Column(length = 1000)
    private String internalNotes;

    // Delivery Address
    private String deliveryRecipientName;
    private String deliveryStreetAddress;
    private String deliveryCity;
    private String deliveryPostalCode;
    private String deliveryCountry;
    private String deliveryAdditionalInstructions;

    // Billing Address
    private String billingName;
    private String billingStreetAddress;
    private String billingCity;
    private String billingPostalCode;
    private String billingCountry;
    private String billingCompanyName;
    private String billingVatNumber;

    // Payment Summary
    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal shippingAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineEntity> orderLines;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("modifiedAt ASC")
    private List<OrderHistoryEntity> modificationHistory;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (orderNumber == null) {
            orderNumber = java.util.UUID.randomUUID().toString(); // Générer un numéro de commande unique
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Méthode utilitaire pour ajouter une ligne de commande
    public void addOrderLine(OrderLineEntity orderLine) {
        orderLines.add(orderLine);
        orderLine.setOrder(this);
    }

    // Méthode utilitaire pour ajouter une entrée d'historique
    public void addModificationHistory(OrderHistoryEntity history) {
        modificationHistory.add(history);
        history.setOrder(this);
    }
}

