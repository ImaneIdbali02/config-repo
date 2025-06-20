package com.ecommerce.order.application.eventhandler;

import com.ecommerce.order.application.service.OrderApplicationService;
import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CartCheckedOutHandler {

    private static final Logger logger = LoggerFactory.getLogger(CartCheckedOutHandler.class);
    
    private final OrderApplicationService orderApplicationService;

    public CartCheckedOutHandler(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @KafkaListener(topics = "cart-events", groupId = "order-service-group")
    public void handleCartCheckedOut(CartCheckedOutEvent event) {
        try {
            logger.info("Traitement de l'événement CartCheckedOut pour le panier: {}", event.getCartId());
            
            // Créer une commande à partir du panier validé
            // Cette logique dépendrait de votre modèle de données spécifique
            
            logger.info("Commande créée avec succès à partir du panier: {}", event.getCartId());
            
        } catch (Exception e) {
            logger.error("Erreur lors du traitement de l'événement CartCheckedOut: {}", e.getMessage(), e);
            // Ici, vous pourriez implémenter une logique de retry ou de dead letter queue
        }
    }

    // Classe interne pour l'événement (à adapter selon votre modèle)
    public static class CartCheckedOutEvent {
        private Long cartId;
        private Long customerId;
        private String sessionId;

        // Constructeurs, getters et setters
        public CartCheckedOutEvent() {}

        public CartCheckedOutEvent(Long cartId, Long customerId, String sessionId) {
            this.cartId = cartId;
            this.customerId = customerId;
            this.sessionId = sessionId;
        }

        public Long getCartId() { return cartId; }
        public void setCartId(Long cartId) { this.cartId = cartId; }

        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
}

