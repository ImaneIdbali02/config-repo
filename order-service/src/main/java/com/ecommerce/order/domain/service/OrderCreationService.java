package com.ecommerce.order.domain.service;

import com.ecommerce.order.domain.model.aggregate.Order;
import com.ecommerce.order.domain.model.entity.OrderLine;
import com.ecommerce.order.domain.model.valueobject.BillingAddress;
import com.ecommerce.order.domain.model.valueobject.DeliveryAddress;
import com.ecommerce.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class OrderCreationService {
    
    private final OrderRepository orderRepository;
    private final OrderValidationService orderValidationService;

    public OrderCreationService(OrderRepository orderRepository, 
                               OrderValidationService orderValidationService) {
        this.orderRepository = orderRepository;
        this.orderValidationService = orderValidationService;
    }

    /**
     * Crée une nouvelle commande
     */
    public Order createOrder(Long customerId, DeliveryAddress deliveryAddress, 
                           BillingAddress billingAddress, List<OrderLine> orderLines) {
        
        // Validation des données
        orderValidationService.validateOrderCreation(customerId, deliveryAddress, 
                                                    billingAddress, orderLines);
        
        // Génération du numéro de commande unique
        String orderNumber = generateUniqueOrderNumber();
        
        // Création de la commande
        Order order = new Order(orderNumber, customerId, deliveryAddress, 
                               billingAddress, orderLines);
        
        // Sauvegarde
        return orderRepository.save(order);
    }

    /**
     * Crée une commande avec des notes client
     */
    public Order createOrderWithNotes(Long customerId, DeliveryAddress deliveryAddress, 
                                    BillingAddress billingAddress, List<OrderLine> orderLines,
                                    String customerNotes) {
        
        Order order = createOrder(customerId, deliveryAddress, billingAddress, orderLines);
        order.setCustomerNotes(customerNotes);
        
        return orderRepository.save(order);
    }

    /**
     * Génère un numéro de commande unique
     */
    private String generateUniqueOrderNumber() {
        String orderNumber;
        do {
            orderNumber = "ORD-" + System.currentTimeMillis() + "-" + 
                         UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (orderRepository.existsByOrderNumber(orderNumber));
        
        return orderNumber;
    }

    /**
     * Valide qu'une commande peut être créée pour un client
     */
    public boolean canCreateOrderForCustomer(Long customerId) {
        // Vérifier s'il n'y a pas trop de commandes en attente pour ce client
        List<Order> pendingOrders = orderRepository.findByCustomerIdAndStatus(
                customerId, com.ecommerce.order.domain.model.valueobject.OrderStatus.PENDING);
        
        return pendingOrders.size() < 5; // Maximum 5 commandes en attente par client
    }
}

