package com.ecommerce.order.application.eventhandler;

import com.ecommerce.order.application.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.application.service.OrderApplicationService;
import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailedHandler {

    private static final Logger logger = LoggerFactory.getLogger(PaymentFailedHandler.class);
    
    private final OrderApplicationService orderApplicationService;

    public PaymentFailedHandler(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @KafkaListener(topics = "payment-events", groupId = "order-service-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        try {
            logger.warn("Traitement de l'échec de paiement pour la commande: {}", event.getOrderId());
            
            // Annuler la commande en cas d'échec de paiement
            String reason = String.format("Échec du paiement - Code: %s, Raison: %s", 
                                        event.getErrorCode(), event.getErrorMessage());
            
            orderApplicationService.cancelOrder(event.getOrderId(), reason, "SYSTEM");
            
            logger.info("Commande {} annulée suite à l'échec du paiement {}", 
                       event.getOrderId(), event.getTransactionId());
            
        } catch (Exception e) {
            logger.error("Erreur lors du traitement de l'échec de paiement pour la commande {}: {}", 
                        event.getOrderId(), e.getMessage(), e);
            // Ici, vous pourriez implémenter une logique de retry ou d'alerte
        }
    }

    // Classe interne pour l'événement
    public static class PaymentFailedEvent {
        private Long orderId;
        private String orderNumber;
        private String transactionId;
        private String paymentMethod;
        private String errorCode;
        private String errorMessage;
        private java.math.BigDecimal attemptedAmount;
        private String currency;

        // Constructeurs, getters et setters
        public PaymentFailedEvent() {}

        public PaymentFailedEvent(Long orderId, String orderNumber, String transactionId, 
                                String paymentMethod, String errorCode, String errorMessage,
                                java.math.BigDecimal attemptedAmount, String currency) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.transactionId = transactionId;
            this.paymentMethod = paymentMethod;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.attemptedAmount = attemptedAmount;
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

        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public java.math.BigDecimal getAttemptedAmount() { return attemptedAmount; }
        public void setAttemptedAmount(java.math.BigDecimal attemptedAmount) { this.attemptedAmount = attemptedAmount; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
}

