package com.ecommerce.order.domain.service;

import com.ecommerce.order.domain.model.aggregate.Order;
import com.ecommerce.order.domain.model.valueobject.PaymentSummary;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class OrderDiscountService {

    /**
     * Applique une remise en pourcentage à une commande
     */
    public Order applyPercentageDiscount(Order order, BigDecimal discountPercentage, String reason) {
        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 || 
            discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Le pourcentage de remise doit être entre 0 et 100");
        }

        BigDecimal currentTotal = order.getPaymentSummary().getTotalAmount();
        BigDecimal discountAmount = currentTotal
                .multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return applyFixedDiscount(order, discountAmount, reason);
    }

    /**
     * Applique une remise fixe à une commande
     */
    public Order applyFixedDiscount(Order order, BigDecimal discountAmount, String reason) {
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant de la remise ne peut pas être négatif");
        }

        PaymentSummary currentSummary = order.getPaymentSummary();
        
        if (discountAmount.compareTo(currentSummary.getTotalAmount()) > 0) {
            throw new IllegalArgumentException("La remise ne peut pas être supérieure au montant total");
        }

        // Créer un nouveau résumé de paiement avec la remise
        PaymentSummary newSummary = new PaymentSummary(
                currentSummary.getTotalAmount(),
                currentSummary.getTaxAmount(),
                currentSummary.getShippingAmount(),
                discountAmount,
                currentSummary.getCurrency()
        );

        order.setPaymentSummary(newSummary);
        
        // Ajouter une note sur la remise appliquée
        String currentNotes = order.getInternalNotes();
        String discountNote = String.format("Remise appliquée: %s %s - Raison: %s", 
                discountAmount, currentSummary.getCurrency(), reason);
        
        if (currentNotes != null && !currentNotes.isEmpty()) {
            order.setInternalNotes(currentNotes + "\n" + discountNote);
        } else {
            order.setInternalNotes(discountNote);
        }

        return order;
    }

    /**
     * Supprime la remise d'une commande
     */
    public Order removeDiscount(Order order, String reason) {
        PaymentSummary currentSummary = order.getPaymentSummary();
        
        if (currentSummary.getDiscountAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("Aucune remise à supprimer");
        }

        // Créer un nouveau résumé de paiement sans remise
        PaymentSummary newSummary = new PaymentSummary(
                currentSummary.getTotalAmount(),
                currentSummary.getTaxAmount(),
                currentSummary.getShippingAmount(),
                BigDecimal.ZERO,
                currentSummary.getCurrency()
        );

        order.setPaymentSummary(newSummary);
        
        // Ajouter une note sur la suppression de la remise
        String currentNotes = order.getInternalNotes();
        String removalNote = String.format("Remise supprimée: %s %s - Raison: %s", 
                currentSummary.getDiscountAmount(), currentSummary.getCurrency(), reason);
        
        if (currentNotes != null && !currentNotes.isEmpty()) {
            order.setInternalNotes(currentNotes + "\n" + removalNote);
        } else {
            order.setInternalNotes(removalNote);
        }

        return order;
    }

    /**
     * Calcule le pourcentage de remise actuel
     */
    public BigDecimal getCurrentDiscountPercentage(Order order) {
        PaymentSummary summary = order.getPaymentSummary();
        
        if (summary.getDiscountAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return summary.getDiscountAmount()
                .multiply(BigDecimal.valueOf(100))
                .divide(summary.getTotalAmount(), 2, RoundingMode.HALF_UP);
    }

    /**
     * Vérifie si une commande a une remise appliquée
     */
    public boolean hasDiscount(Order order) {
        return order.getPaymentSummary().getDiscountAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Applique une remise de fidélité basée sur le nombre de commandes du client
     */
    public Order applyLoyaltyDiscount(Order order, int customerOrderCount) {
        BigDecimal discountPercentage = calculateLoyaltyDiscountPercentage(customerOrderCount);
        
        if (discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            return applyPercentageDiscount(order, discountPercentage, 
                    "Remise fidélité - " + customerOrderCount + " commandes");
        }
        
        return order;
    }

    /**
     * Calcule le pourcentage de remise de fidélité
     */
    private BigDecimal calculateLoyaltyDiscountPercentage(int orderCount) {
        if (orderCount >= 50) {
            return BigDecimal.valueOf(15); // 15% pour 50+ commandes
        } else if (orderCount >= 20) {
            return BigDecimal.valueOf(10); // 10% pour 20+ commandes
        } else if (orderCount >= 10) {
            return BigDecimal.valueOf(5);  // 5% pour 10+ commandes
        } else {
            return BigDecimal.ZERO;        // Pas de remise
        }
    }
}

