package com.ecommerce.order.domain.model.valueobject;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class PaymentSummary {
    
    @NotNull(message = "Le montant total est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant total doit être positif")
    private final BigDecimal totalAmount;
    
    @NotNull(message = "Le montant des taxes est obligatoire")
    @DecimalMin(value = "0.0", message = "Le montant des taxes ne peut pas être négatif")
    private final BigDecimal taxAmount;
    
    @NotNull(message = "Le montant de livraison est obligatoire")
    @DecimalMin(value = "0.0", message = "Le montant de livraison ne peut pas être négatif")
    private final BigDecimal shippingAmount;
    
    @NotNull(message = "Le montant de remise est obligatoire")
    @DecimalMin(value = "0.0", message = "Le montant de remise ne peut pas être négatif")
    private final BigDecimal discountAmount;
    
    @NotNull(message = "Le montant net est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant net doit être positif")
    private final BigDecimal netAmount;
    
    @NotNull(message = "La devise est obligatoire")
    private final String currency;

    public PaymentSummary(BigDecimal totalAmount, BigDecimal taxAmount, BigDecimal shippingAmount, 
                         BigDecimal discountAmount, String currency) {
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount;
        this.shippingAmount = shippingAmount;
        this.discountAmount = discountAmount;
        this.currency = currency;
        this.netAmount = calculateNetAmount();
    }

    private BigDecimal calculateNetAmount() {
        return totalAmount.add(taxAmount).add(shippingAmount).subtract(discountAmount);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentSummary that = (PaymentSummary) o;
        return Objects.equals(totalAmount, that.totalAmount) &&
               Objects.equals(taxAmount, that.taxAmount) &&
               Objects.equals(shippingAmount, that.shippingAmount) &&
               Objects.equals(discountAmount, that.discountAmount) &&
               Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalAmount, taxAmount, shippingAmount, discountAmount, currency);
    }

    @Override
    public String toString() {
        return String.format("Total: %s %s (Net: %s %s)", 
                totalAmount, currency, netAmount, currency);
    }
}

