package com.ecommerce.order.application.eventhandler;

import com.ecommerce.order.application.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.application.service.OrderApplicationService;
import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentDeliveredHandler {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentDeliveredHandler.class);
    
    private final OrderApplicationService orderApplicationService;

    public ShipmentDeliveredHandler(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @KafkaListener(topics = "shipment-events", groupId = "order-service-group")
    public void handleShipmentDelivered(ShipmentDeliveredEvent event) {
        try {
            logger.info("Traitement de la livraison pour la commande: {}", event.getOrderId());
            
            // Mettre à jour le statut de la commande à DELIVERED
            UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(
                    OrderStatus.DELIVERED, 
                    "Livraison confirmée - Reçu par: " + event.getReceivedBy()
            );
            
            orderApplicationService.updateOrderStatus(event.getOrderId(), request, "SYSTEM");
            
            logger.info("Commande {} marquée comme livrée, reçue par {}", 
                       event.getOrderId(), event.getReceivedBy());
            
        } catch (Exception e) {
            logger.error("Erreur lors du traitement de la livraison pour la commande {}: {}", 
                        event.getOrderId(), e.getMessage(), e);
        }
    }

    // Classe interne pour l'événement
    public static class ShipmentDeliveredEvent {
        private Long orderId;
        private String orderNumber;
        private String shipmentId;
        private String trackingNumber;
        private java.time.LocalDateTime deliveredAt;
        private String receivedBy;
        private String deliveryLocation;
        private String deliveryNotes;

        // Constructeurs, getters et setters
        public ShipmentDeliveredEvent() {}

        public ShipmentDeliveredEvent(Long orderId, String orderNumber, String shipmentId, 
                                    String trackingNumber, java.time.LocalDateTime deliveredAt,
                                    String receivedBy, String deliveryLocation, String deliveryNotes) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.shipmentId = shipmentId;
            this.trackingNumber = trackingNumber;
            this.deliveredAt = deliveredAt;
            this.receivedBy = receivedBy;
            this.deliveryLocation = deliveryLocation;
            this.deliveryNotes = deliveryNotes;
        }

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

        public String getShipmentId() { return shipmentId; }
        public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }

        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

        public java.time.LocalDateTime getDeliveredAt() { return deliveredAt; }
        public void setDeliveredAt(java.time.LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

        public String getReceivedBy() { return receivedBy; }
        public void setReceivedBy(String receivedBy) { this.receivedBy = receivedBy; }

        public String getDeliveryLocation() { return deliveryLocation; }
        public void setDeliveryLocation(String deliveryLocation) { this.deliveryLocation = deliveryLocation; }

        public String getDeliveryNotes() { return deliveryNotes; }
        public void setDeliveryNotes(String deliveryNotes) { this.deliveryNotes = deliveryNotes; }
    }
}

