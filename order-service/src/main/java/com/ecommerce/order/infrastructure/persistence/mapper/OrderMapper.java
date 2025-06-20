package com.ecommerce.order.infrastructure.persistence.mapper;

import com.ecommerce.order.domain.model.aggregate.Order;
import com.ecommerce.order.domain.model.entity.OrderLine;
import com.ecommerce.order.domain.model.entity.OrderModificationHistory;
import com.ecommerce.order.domain.model.valueobject.BillingAddress;
import com.ecommerce.order.domain.model.valueobject.DeliveryAddress;
import com.ecommerce.order.domain.model.valueobject.PaymentSummary;
import com.ecommerce.order.infrastructure.persistence.entity.OrderEntity;
import com.ecommerce.order.infrastructure.persistence.entity.OrderHistoryEntity;
import com.ecommerce.order.infrastructure.persistence.entity.OrderLineEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderEntity toOrderEntity(Order order) {
        if (order == null) {
            return null;
        }

        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .confirmedAt(order.getConfirmedAt())
                .shippedAt(order.getShippedAt())
                .deliveredAt(order.getDeliveredAt())
                .customerNotes(order.getCustomerNotes())
                .internalNotes(order.getInternalNotes())
                .totalAmount(order.getPaymentSummary().getTotalAmount())
                .taxAmount(order.getPaymentSummary().getTaxAmount())
                .shippingAmount(order.getPaymentSummary().getShippingAmount())
                .discountAmount(order.getPaymentSummary().getDiscountAmount())
                .currency(order.getPaymentSummary().getCurrency())
                .build();

        // Adresses
        if (order.getDeliveryAddress() != null) {
            orderEntity.setDeliveryRecipientName(order.getDeliveryAddress().getRecipientName());
            orderEntity.setDeliveryStreetAddress(order.getDeliveryAddress().getStreetAddress());
            orderEntity.setDeliveryCity(order.getDeliveryAddress().getCity());
            orderEntity.setDeliveryPostalCode(order.getDeliveryAddress().getPostalCode());
            orderEntity.setDeliveryCountry(order.getDeliveryAddress().getCountry());
            orderEntity.setDeliveryAdditionalInstructions(order.getDeliveryAddress().getAdditionalInstructions());
        }
        if (order.getBillingAddress() != null) {
            orderEntity.setBillingName(order.getBillingAddress().getBillingName());
            orderEntity.setBillingStreetAddress(order.getBillingAddress().getStreetAddress());
            orderEntity.setBillingCity(order.getBillingAddress().getCity());
            orderEntity.setBillingPostalCode(order.getBillingAddress().getPostalCode());
            orderEntity.setBillingCountry(order.getBillingAddress().getCountry());
            orderEntity.setBillingCompanyName(order.getBillingAddress().getCompanyName());
            orderEntity.setBillingVatNumber(order.getBillingAddress().getVatNumber());
        }

        // Lignes de commande
        if (order.getOrderLines() != null) {
            orderEntity.setOrderLines(order.getOrderLines().stream()
                    .map(this::toOrderLineEntity)
                    .collect(Collectors.toList()));
            orderEntity.getOrderLines().forEach(line -> line.setOrder(orderEntity));
        }

        // Historique de modification
        if (order.getModificationHistory() != null) {
            orderEntity.setModificationHistory(order.getModificationHistory().stream()
                    .map(this::toOrderHistoryEntity)
                    .collect(Collectors.toList()));
            orderEntity.getModificationHistory().forEach(history -> history.setOrder(orderEntity));
        }

        return orderEntity;
    }

    public Order toOrder(OrderEntity orderEntity) {
        if (orderEntity == null) {
            return null;
        }

        Order order = new Order(
                orderEntity.getOrderNumber(),
                orderEntity.getCustomerId(),
                toDeliveryAddress(orderEntity),
                toBillingAddress(orderEntity),
                orderEntity.getOrderLines() != null ?
                        orderEntity.getOrderLines().stream()
                                .map(this::toOrderLine)
                                .collect(Collectors.toList()) : null
        );

        // Set additional fields via setters (if available)
        order.setId(orderEntity.getId());
        order.setStatus(orderEntity.getStatus());
        order.setCreatedAt(orderEntity.getCreatedAt());
        order.setUpdatedAt(orderEntity.getUpdatedAt());
        order.setConfirmedAt(orderEntity.getConfirmedAt());
        order.setShippedAt(orderEntity.getShippedAt());
        order.setDeliveredAt(orderEntity.getDeliveredAt());
        order.setCustomerNotes(orderEntity.getCustomerNotes());
        order.setInternalNotes(orderEntity.getInternalNotes());
        order.setPaymentSummary(toPaymentSummary(orderEntity));

        return order;
    }

    public OrderLineEntity toOrderLineEntity(OrderLine orderLine) {
        if (orderLine == null) {
            return null;
        }
        return OrderLineEntity.builder()
                .id(orderLine.getId())
                .productId(orderLine.getProductId())
                .productName(orderLine.getProductName())
                .productSku(orderLine.getProductSku())
                .quantity(orderLine.getQuantity())
                .unitPrice(orderLine.getUnitPrice())
                .totalPrice(orderLine.getTotalPrice())
                .productImageUrl(orderLine.getProductImageUrl())
                .productDescription(orderLine.getProductDescription())
                .build();
    }

    public OrderLine toOrderLine(OrderLineEntity orderLineEntity) {
        if (orderLineEntity == null) {
            return null;
        }

        // Utilise le constructeur avec les 5 paramètres obligatoires
        OrderLine orderLine = new OrderLine(
                orderLineEntity.getProductId(),
                orderLineEntity.getProductName(),
                orderLineEntity.getProductSku(),
                orderLineEntity.getQuantity(),
                orderLineEntity.getUnitPrice()
        );

        // Définit les autres propriétés via les setters
        orderLine.setId(orderLineEntity.getId());
        orderLine.setTotalPrice(orderLineEntity.getTotalPrice());
        orderLine.setProductImageUrl(orderLineEntity.getProductImageUrl());
        orderLine.setProductDescription(orderLineEntity.getProductDescription());

        return orderLine;
    }

    public OrderHistoryEntity toOrderHistoryEntity(OrderModificationHistory history) {
        if (history == null) {
            return null;
        }
        return OrderHistoryEntity.builder()
                .id(history.getId())
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .reason(history.getReason())
                .modifiedBy(history.getModifiedBy())
                .modifiedAt(history.getModifiedAt())
                .additionalNotes(history.getAdditionalNotes())
                .build();
    }

    public OrderModificationHistory toOrderModificationHistory(OrderHistoryEntity historyEntity) {
        if (historyEntity == null) {
            return null;
        }

        OrderModificationHistory history = new OrderModificationHistory(
                historyEntity.getPreviousStatus(),
                historyEntity.getNewStatus(),
                historyEntity.getReason(),
                historyEntity.getModifiedBy()
        );

        // Utilisation des setters pour les champs supplémentaires
        history.setId(historyEntity.getId());
        history.setModifiedAt(historyEntity.getModifiedAt());
        history.setAdditionalNotes(historyEntity.getAdditionalNotes());

        return history;
    }

    private DeliveryAddress toDeliveryAddress(OrderEntity orderEntity) {
        return new DeliveryAddress(
                orderEntity.getDeliveryRecipientName(),
                orderEntity.getDeliveryStreetAddress(),
                orderEntity.getDeliveryCity(),
                orderEntity.getDeliveryPostalCode(),
                orderEntity.getDeliveryCountry(),
                orderEntity.getDeliveryAdditionalInstructions()
        );
    }

    private BillingAddress toBillingAddress(OrderEntity orderEntity) {
        return new BillingAddress(
                orderEntity.getBillingName(),
                orderEntity.getBillingStreetAddress(),
                orderEntity.getBillingCity(),
                orderEntity.getBillingPostalCode(),
                orderEntity.getBillingCountry(),
                orderEntity.getBillingCompanyName(),
                orderEntity.getBillingVatNumber()
        );
    }

    private PaymentSummary toPaymentSummary(OrderEntity orderEntity) {
        return new PaymentSummary(
                orderEntity.getTotalAmount(),
                orderEntity.getTaxAmount(),
                orderEntity.getShippingAmount(),
                orderEntity.getDiscountAmount(),
                orderEntity.getCurrency()
        );
    }
}

