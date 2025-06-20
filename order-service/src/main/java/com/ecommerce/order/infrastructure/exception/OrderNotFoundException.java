package com.ecommerce.order.infrastructure.exception;

public class OrderNotFoundException extends RuntimeException {
    
    public OrderNotFoundException(String message) {
        super(message);
    }
    
    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public OrderNotFoundException(Long orderId) {
        super("Commande non trouvée avec l'ID: " + orderId);
    }
    
    public OrderNotFoundException(String orderNumber, boolean byNumber) {
        super("Commande non trouvée avec le numéro: " + orderNumber);
    }
}

