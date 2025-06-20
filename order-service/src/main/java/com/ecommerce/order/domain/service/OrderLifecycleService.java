package com.ecommerce.order.domain.service;

import com.ecommerce.order.domain.model.aggregate.Order;
import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import com.ecommerce.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderLifecycleService {

    private final OrderRepository orderRepository;

    public OrderLifecycleService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Fait progresser une commande dans son cycle de vie
     */
    public Order progressOrder(Long orderId, OrderStatus targetStatus, String reason, String modifiedBy) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée: " + orderId));

        // Vérifier si la transition est possible
        if (!order.getStatus().canTransitionTo(targetStatus)) {
            throw new IllegalStateException(
                String.format("Transition impossible de %s vers %s", order.getStatus(), targetStatus));
        }

        // Effectuer la transition
        order.updateStatus(targetStatus, reason, modifiedBy);

        return orderRepository.save(order);
    }

    /**
     * Confirme une commande
     */
    public Order confirmOrder(Long orderId, String confirmedBy) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée: " + orderId));

        order.confirmOrder(confirmedBy);
        return orderRepository.save(order);
    }

    /**
     * Annule une commande si possible
     */
    public Order cancelOrder(Long orderId, String reason, String cancelledBy) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée: " + orderId));

        if (!order.canBeCancelled()) {
            throw new IllegalStateException("Cette commande ne peut plus être annulée");
        }

        order.updateStatus(OrderStatus.CANCELLED, reason, cancelledBy);
        return orderRepository.save(order);
    }

    /**
     * Marque une commande comme prête pour expédition
     */
    public Order markReadyForShipment(Long orderId, String modifiedBy) {
        return progressOrder(orderId, OrderStatus.READY_FOR_SHIPMENT, 
                           "Commande prête pour expédition", modifiedBy);
    }

    /**
     * Marque une commande comme expédiée
     */
    public Order markShipped(Long orderId, String modifiedBy) {
        return progressOrder(orderId, OrderStatus.SHIPPED, 
                           "Commande expédiée", modifiedBy);
    }

    /**
     * Marque une commande comme livrée
     */
    public Order markDelivered(Long orderId, String modifiedBy) {
        return progressOrder(orderId, OrderStatus.DELIVERED, 
                           "Commande livrée", modifiedBy);
    }

    /**
     * Vérifie si une commande peut être modifiée
     */
    public boolean canModifyOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée: " + orderId));

        return order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED;
    }
}

