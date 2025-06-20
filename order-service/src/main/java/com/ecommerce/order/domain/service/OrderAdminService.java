package com.ecommerce.order.domain.service;

import com.ecommerce.order.domain.model.aggregate.Order;
import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import com.ecommerce.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderAdminService {

    private final OrderRepository orderRepository;
    private final OrderLifecycleService orderLifecycleService;

    public OrderAdminService(OrderRepository orderRepository, 
                           OrderLifecycleService orderLifecycleService) {
        this.orderRepository = orderRepository;
        this.orderLifecycleService = orderLifecycleService;
    }

    /**
     * Force le changement de statut d'une commande (admin uniquement)
     */
    public Order forceStatusChange(Long orderId, OrderStatus newStatus, String reason, String adminUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée: " + orderId));

        // Les admins peuvent forcer n'importe quelle transition
        order.updateStatus(newStatus, "ADMIN: " + reason, adminUser);
        return orderRepository.save(order);
    }

    /**
     * Ajoute des notes internes à une commande
     */
    public Order addInternalNotes(Long orderId, String notes, String adminUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée: " + orderId));

        String existingNotes = order.getInternalNotes();
        String timestamp = LocalDateTime.now().toString();
        String newNotes = String.format("[%s - %s] %s", timestamp, adminUser, notes);
        
        if (existingNotes != null && !existingNotes.isEmpty()) {
            newNotes = existingNotes + "\n" + newNotes;
        }
        
        order.setInternalNotes(newNotes);
        return orderRepository.save(order);
    }

    /**
     * Récupère toutes les commandes en attente depuis plus de X heures
     */
    public List<Order> getPendingOrdersOlderThan(int hours) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(hours);
        return orderRepository.findPendingOrdersOlderThan(threshold);
    }

    /**
     * Récupère les statistiques des commandes par statut
     */
    public OrderStatistics getOrderStatistics() {
        OrderStatistics stats = new OrderStatistics();
        
        for (OrderStatus status : OrderStatus.values()) {
            long count = orderRepository.countByStatus(status);
            stats.addStatusCount(status, count);
        }
        
        return stats;
    }

    /**
     * Annule en masse les commandes en attente anciennes
     */
    public List<Order> cancelOldPendingOrders(int hoursThreshold, String adminUser) {
        List<Order> oldOrders = getPendingOrdersOlderThan(hoursThreshold);
        
        return oldOrders.stream()
                .map(order -> {
                    try {
                        return orderLifecycleService.cancelOrder(
                            order.getId(), 
                            "Annulation automatique - commande trop ancienne", 
                            adminUser
                        );
                    } catch (Exception e) {
                        // Log l'erreur mais continue avec les autres commandes
                        return null;
                    }
                })
                .filter(order -> order != null)
                .toList();
    }

    /**
     * Classe pour les statistiques des commandes
     */
    public static class OrderStatistics {
        private final java.util.Map<OrderStatus, Long> statusCounts = new java.util.HashMap<>();
        
        public void addStatusCount(OrderStatus status, Long count) {
            statusCounts.put(status, count);
        }
        
        public Long getCountForStatus(OrderStatus status) {
            return statusCounts.getOrDefault(status, 0L);
        }
        
        public java.util.Map<OrderStatus, Long> getAllCounts() {
            return new java.util.HashMap<>(statusCounts);
        }
        
        public Long getTotalOrders() {
            return statusCounts.values().stream().mapToLong(Long::longValue).sum();
        }
    }
}

