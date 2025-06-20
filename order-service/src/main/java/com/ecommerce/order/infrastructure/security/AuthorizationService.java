package com.ecommerce.order.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public boolean canAccessOrder(Authentication authentication, Long orderId) {
        // Logique d'autorisation complexe ici
        // Par exemple, vérifier si l'utilisateur est le propriétaire de la commande
        // ou s'il a un rôle d'administrateur
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        // Ou si l'utilisateur est le client de la commande (nécessiterait de récupérer la commande)
        // Order order = orderRepository.findById(orderId).orElse(null);
        // return order != null && order.getCustomerId().equals(authentication.getName());
    }

    public boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isCustomer(Authentication authentication, Long customerId) {
        // Vérifie si l'utilisateur authentifié est le client spécifié
        return authentication.getName().equals(String.valueOf(customerId));
    }
}

