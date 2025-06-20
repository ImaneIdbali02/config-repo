package com.ecommerce.order.infrastructure.exception;

public class UnauthorizedAccessException extends RuntimeException {
    
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UnauthorizedAccessException(Long orderId, String userId) {
        super("L'utilisateur " + userId + " n'est pas autorisé à accéder à la commande " + orderId);
    }
}

