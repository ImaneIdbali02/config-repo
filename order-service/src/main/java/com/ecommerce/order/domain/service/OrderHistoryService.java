package com.ecommerce.order.domain.service;

import com.ecommerce.order.domain.model.aggregate.Order;
import com.ecommerce.order.domain.model.entity.OrderModificationHistory;
import com.ecommerce.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderHistoryService {

    private final OrderRepository orderRepository;

    public OrderHistoryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Récupère l'historique complet d'une commande
     */
    public List<OrderModificationHistory> getOrderHistory(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée: " + orderId));

        return order.getModificationHistory();
    }

    /**
     * Récupère l'historique d'une commande pour une période donnée
     */
    public List<OrderModificationHistory> getOrderHistoryBetween(Long orderId, 
                                                               LocalDateTime startDate, 
                                                               LocalDateTime endDate) {
        List<OrderModificationHistory> fullHistory = getOrderHistory(orderId);
        
        return fullHistory.stream()
                .filter(history -> !history.getModifiedAt().isBefore(startDate) && 
                                 !history.getModifiedAt().isAfter(endDate))
                .toList();
    }

    /**
     * Récupère l'historique des modifications effectuées par un utilisateur spécifique
     */
    public List<OrderModificationHistory> getOrderHistoryByUser(Long orderId, String userId) {
        List<OrderModificationHistory> fullHistory = getOrderHistory(orderId);
        
        return fullHistory.stream()
                .filter(history -> history.getModifiedBy().equals(userId))
                .toList();
    }

    /**
     * Compte le nombre de modifications d'une commande
     */
    public long countOrderModifications(Long orderId) {
        return getOrderHistory(orderId).size();
    }

    /**
     * Récupère la dernière modification d'une commande
     */
    public OrderModificationHistory getLastModification(Long orderId) {
        List<OrderModificationHistory> history = getOrderHistory(orderId);
        
        if (history.isEmpty()) {
            return null;
        }
        
        return history.stream()
                .max((h1, h2) -> h1.getModifiedAt().compareTo(h2.getModifiedAt()))
                .orElse(null);
    }

    /**
     * Vérifie si une commande a été modifiée récemment
     */
    public boolean hasRecentModifications(Long orderId, int hoursBack) {
        OrderModificationHistory lastModification = getLastModification(orderId);
        
        if (lastModification == null) {
            return false;
        }
        
        LocalDateTime threshold = LocalDateTime.now().minusHours(hoursBack);
        return lastModification.getModifiedAt().isAfter(threshold);
    }
}

