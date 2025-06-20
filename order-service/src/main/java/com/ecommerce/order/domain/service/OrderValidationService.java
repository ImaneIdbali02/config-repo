package com.ecommerce.order.domain.service;

import com.ecommerce.order.domain.model.entity.OrderLine;
import com.ecommerce.order.domain.model.valueobject.BillingAddress;
import com.ecommerce.order.domain.model.valueobject.DeliveryAddress;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderValidationService {

    /**
     * Valide les données nécessaires à la création d'une commande
     */
    public void validateOrderCreation(Long customerId, DeliveryAddress deliveryAddress, 
                                    BillingAddress billingAddress, List<OrderLine> orderLines) {
        
        validateCustomerId(customerId);
        validateDeliveryAddress(deliveryAddress);
        validateBillingAddress(billingAddress);
        validateOrderLines(orderLines);
    }

    /**
     * Valide l'ID du client
     */
    private void validateCustomerId(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw new IllegalArgumentException("L'ID du client doit être un nombre positif");
        }
    }

    /**
     * Valide l'adresse de livraison
     */
    private void validateDeliveryAddress(DeliveryAddress deliveryAddress) {
        if (deliveryAddress == null) {
            throw new IllegalArgumentException("L'adresse de livraison est obligatoire");
        }
        
        if (isBlank(deliveryAddress.getRecipientName())) {
            throw new IllegalArgumentException("Le nom du destinataire est obligatoire");
        }
        
        if (isBlank(deliveryAddress.getStreetAddress())) {
            throw new IllegalArgumentException("L'adresse de rue est obligatoire");
        }
        
        if (isBlank(deliveryAddress.getCity())) {
            throw new IllegalArgumentException("La ville est obligatoire");
        }
        
        if (isBlank(deliveryAddress.getPostalCode())) {
            throw new IllegalArgumentException("Le code postal est obligatoire");
        }
        
        if (isBlank(deliveryAddress.getCountry())) {
            throw new IllegalArgumentException("Le pays est obligatoire");
        }
    }

    /**
     * Valide l'adresse de facturation
     */
    private void validateBillingAddress(BillingAddress billingAddress) {
        if (billingAddress == null) {
            throw new IllegalArgumentException("L'adresse de facturation est obligatoire");
        }
        
        if (isBlank(billingAddress.getBillingName())) {
            throw new IllegalArgumentException("Le nom de facturation est obligatoire");
        }
        
        if (isBlank(billingAddress.getStreetAddress())) {
            throw new IllegalArgumentException("L'adresse de facturation est obligatoire");
        }
        
        if (isBlank(billingAddress.getCity())) {
            throw new IllegalArgumentException("La ville de facturation est obligatoire");
        }
        
        if (isBlank(billingAddress.getPostalCode())) {
            throw new IllegalArgumentException("Le code postal de facturation est obligatoire");
        }
        
        if (isBlank(billingAddress.getCountry())) {
            throw new IllegalArgumentException("Le pays de facturation est obligatoire");
        }
    }

    /**
     * Valide les lignes de commande
     */
    private void validateOrderLines(List<OrderLine> orderLines) {
        if (orderLines == null || orderLines.isEmpty()) {
            throw new IllegalArgumentException("La commande doit contenir au moins une ligne");
        }
        
        if (orderLines.size() > 50) {
            throw new IllegalArgumentException("Une commande ne peut pas contenir plus de 50 lignes");
        }
        
        for (OrderLine orderLine : orderLines) {
            validateOrderLine(orderLine);
        }
        
        // Vérifier qu'il n'y a pas de doublons de produits
        long distinctProductCount = orderLines.stream()
                .mapToLong(OrderLine::getProductId)
                .distinct()
                .count();
        
        if (distinctProductCount != orderLines.size()) {
            throw new IllegalArgumentException("La commande contient des produits en double");
        }
    }

    /**
     * Valide une ligne de commande individuelle
     */
    private void validateOrderLine(OrderLine orderLine) {
        if (orderLine == null) {
            throw new IllegalArgumentException("Ligne de commande invalide");
        }
        
        if (orderLine.getProductId() == null || orderLine.getProductId() <= 0) {
            throw new IllegalArgumentException("L'ID du produit doit être un nombre positif");
        }
        
        if (isBlank(orderLine.getProductName())) {
            throw new IllegalArgumentException("Le nom du produit est obligatoire");
        }
        
        if (isBlank(orderLine.getProductSku())) {
            throw new IllegalArgumentException("Le SKU du produit est obligatoire");
        }
        
        if (orderLine.getQuantity() == null || orderLine.getQuantity() <= 0) {
            throw new IllegalArgumentException("La quantité doit être un nombre positif");
        }
        
        if (orderLine.getQuantity() > 100) {
            throw new IllegalArgumentException("La quantité ne peut pas dépasser 100 par ligne");
        }
        
        if (orderLine.getUnitPrice() == null || orderLine.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être positif");
        }
        
        if (orderLine.getUnitPrice().compareTo(BigDecimal.valueOf(10000)) > 0) {
            throw new IllegalArgumentException("Le prix unitaire ne peut pas dépasser 10 000 €");
        }
    }

    /**
     * Vérifie si une chaîne est vide ou nulle
     */
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Valide qu'une quantité est acceptable pour une mise à jour
     */
    public void validateQuantityUpdate(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("La nouvelle quantité doit être un nombre positif");
        }
        
        if (newQuantity > 100) {
            throw new IllegalArgumentException("La quantité ne peut pas dépasser 100");
        }
    }
}

