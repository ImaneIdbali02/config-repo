package com.ecommerce.order.application.eventhandler;

import com.ecommerce.order.application.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.application.service.OrderApplicationService;
import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentCreatedHandler {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentCreatedHandler.class);
    
    private final OrderApplicationService orderApplicationService;

    public ShipmentCreatedHandler(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @KafkaListener(topics = "shipment-events", groupId = "order-service-group")
    public void handleShipmentCreated(ShipmentCreatedEvent event) {
        try {
            logger.info("Traitement de la création d'expédition pour la commande: {}", event.getOrderId());
            
            // Mettre à jour le statut de la commande à SHIPPED
            UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(
                    OrderStatus.SHIPPED, 
                    "Expédition créée - Numéro de suivi: " + event.getTrackingNumber()
            );
            
            orderApplicationService.updateOrderStatus(event.getOrderId(), request, "SYSTEM");
            
            logger.info("Commande {} marquée comme expédiée avec le numéro de suivi {}", 
                       event.getOrderId(), event.getTrackingNumber());
            
        } catch (Exception e) {
            logger.error("Erreur lors du traitement de la création d'expédition pour la commande {}: {}", 
                        event.getOrderId(), e.getMessage(), e);
        }
    }

    // Classe interne pour l'événement
    public static class ShipmentCreatedEvent {
        private Long orderId;
        private String orderNumber;
        private String shipmentId;
        private String trackingNumber;
        private String carrier;
        private String shippingMethod;
        private java.time.LocalDateTime estimatedDeliveryDate;

        // Constructeurs, getters et setters
        public ShipmentCreatedEvent() {}

        public ShipmentCreatedEvent(Long orderId, String orderNumber, String shipmentId, 
                                  String trackingNumber, String carrier, String shippingMethod,
                                  java.time.LocalDateTime estimatedDeliveryDate) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.shipmentId = shipmentId;
            this.trackingNumber = trackingNumber;
            this.carrier = carrier;
            this.shippingMethod = shippingMethod;
            this.estimatedDeliveryDate = estimatedDeliveryDate;
        }

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

        public String getShipmentId() { return shipmentId; }
        public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }

        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

        public String getCarrier() { return carrier; }
        public void setCarrier(String carrier) { this.carrier = carrier; }

        public String getShippingMethod() { return shippingMethod; }
        public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }

        public java.time.LocalDateTime getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
        public void setEstimatedDeliveryDate(java.time.LocalDateTime estimatedDeliveryDate) { 
            this.estimatedDeliveryDate = estimatedDeliveryDate; 
        }
    }
}

