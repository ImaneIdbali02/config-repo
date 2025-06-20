package com.ecommerce.order.domain.model.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class OrderLine {
    
    @NotNull(message = "L'ID de la ligne de commande est obligatoire")
    private Long id;
    
    @NotNull(message = "L'ID du produit est obligatoire")
    private Long productId;
    
    @NotBlank(message = "Le nom du produit est obligatoire")
    private String productName;
    
    @NotBlank(message = "Le SKU du produit est obligatoire")
    private String productSku;
    
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins de 1")
    private Integer quantity;
    
    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être positif")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Le prix total est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix total doit être positif")
    private BigDecimal totalPrice;
    
    private String productImageUrl;
    private String productDescription;

    public OrderLine() {
        // Constructeur par défaut pour JPA
    }

    public OrderLine(Long productId, String productName, String productSku, 
                    Integer quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.productSku = productSku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = calculateTotalPrice();
    }

    private BigDecimal calculateTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void updateQuantity(Integer newQuantity) {
        if (newQuantity < 1) {
            throw new IllegalArgumentException("La quantité doit être au moins de 1");
        }
        this.quantity = newQuantity;
        this.totalPrice = calculateTotalPrice();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLine orderLine = (OrderLine) o;
        return Objects.equals(id, orderLine.id) &&
               Objects.equals(productId, orderLine.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productId);
    }

    @Override
    public String toString() {
        return String.format("OrderLine{id=%d, productName='%s', quantity=%d, unitPrice=%s}", 
                id, productName, quantity, unitPrice);
    }
}

