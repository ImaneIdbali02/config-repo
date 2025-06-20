package com.ecommerce.order.domain.repository;

import com.ecommerce.order.domain.model.aggregate.Order;
import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    
    /**
     * Sauvegarde une commande
     */
    Order save(Order order);
    
    /**
     * Trouve une commande par son ID
     */
    Optional<Order> findById(Long id);
    
    /**
     * Trouve une commande par son numéro
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * Trouve toutes les commandes d'un client
     */
    List<Order> findByCustomerId(Long customerId);
    
    /**
     * Trouve les commandes par statut
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Trouve les commandes créées entre deux dates
     */
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Trouve les commandes d'un client avec un statut spécifique
     */
    List<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status);
    
    /**
     * Trouve les commandes en attente de traitement (anciennes)
     */
    List<Order> findPendingOrdersOlderThan(LocalDateTime dateTime);
    
    /**
     * Compte le nombre de commandes par statut
     */
    long countByStatus(OrderStatus status);
    
    /**
     * Supprime une commande (utilisé uniquement pour les tests)
     */
    void deleteById(Long id);
    
    /**
     * Vérifie si une commande existe
     */
    boolean existsById(Long id);
    
    /**
     * Vérifie si un numéro de commande existe déjà
     */
    boolean existsByOrderNumber(String orderNumber);
}

