package com.ecommerce.order.application.eventhandler;

import com.ecommerce.order.application.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.application.service.OrderApplicationService;
import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentSucceededHandler {

    private static final Logger logger = LoggerFactory.getLogger(PaymentSucceededHandler.class);
    
    private final OrderApplicationService orderApplicationService;

    public PaymentSucceededHandler(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @KafkaListener(topics = "payment-events", groupId = "order-service-group")
    public void handlePaymentSucceeded(PaymentSucceededEvent event) {
        try {
            logger.info("Traitement du paiement réussi pour la commande: {}", event.getOrderId());
            
            // Mettre à jour le statut de la commande à PAID
            UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(
                    OrderStatus.PAID, 
                    "Paiement confirmé - Transaction: " + event.getTransactionId()
            );
            
            orderApplicationService.updateOrderStatus(event.getOrderId(), request, "SYSTEM");
            
            logger.info("Commande {} marquée comme payée suite au paiement {}", 
                       event.getOrderId(), event.getTransactionId());
            
        } catch (Exception e) {
            logger.error("Erreur lors du traitement du paiement réussi pour la commande {}: {}", 
                        event.getOrderId(), e.getMessage(), e);
            // Ici, vous pourriez implémenter une logique de retry ou de compensation
        }
    }

    // Classe interne pour l'événement
    public static class PaymentSucceededEvent {
        private Long orderId;
        private String orderNumber;
        private String transactionId;
        private String paymentMethod;
        private java.math.BigDecimal amount;
        private String currency;

        // Constructeurs, getters et setters
        public PaymentSucceededEvent() {}

        public PaymentSucceededEvent(Long orderId, String orderNumber, String transactionId, 
                                   String paymentMethod, java.math.BigDecimal amount, String currency) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.transactionId = transactionId;
            this.paymentMethod = paymentMethod;
            this.amount = amount;
            this.currency = currency;
        }

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public java.math.BigDecimal getAmount() { return amount; }
        public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
}

