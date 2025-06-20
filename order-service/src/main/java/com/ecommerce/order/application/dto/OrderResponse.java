package com.ecommerce.order.application.dto;

import com.ecommerce.order.domain.model.valueobject.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    
    private Long id;
    private String orderNumber;
    private Long customerId;
    private OrderStatus status;
    private String statusDisplayName;
    
    private DeliveryAddressResponse deliveryAddress;
    private BillingAddressResponse billingAddress;
    private PaymentSummaryResponse paymentSummary;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    
    private String customerNotes;
    private String internalNotes;
    
    private List<OrderLineResponse> orderLines;
    private List<OrderModificationHistoryResponse> modificationHistory;

    // Constructeurs
    public OrderResponse() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { 
        this.status = status; 
        this.statusDisplayName = status != null ? status.getDisplayName() : null;
    }
    
    public String getStatusDisplayName() { return statusDisplayName; }
    public void setStatusDisplayName(String statusDisplayName) { this.statusDisplayName = statusDisplayName; }
    
    public DeliveryAddressResponse getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(DeliveryAddressResponse deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public BillingAddressResponse getBillingAddress() { return billingAddress; }
    public void setBillingAddress(BillingAddressResponse billingAddress) { this.billingAddress = billingAddress; }
    
    public PaymentSummaryResponse getPaymentSummary() { return paymentSummary; }
    public void setPaymentSummary(PaymentSummaryResponse paymentSummary) { this.paymentSummary = paymentSummary; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
    
    public LocalDateTime getShippedAt() { return shippedAt; }
    public void setShippedAt(LocalDateTime shippedAt) { this.shippedAt = shippedAt; }
    
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    
    public String getCustomerNotes() { return customerNotes; }
    public void setCustomerNotes(String customerNotes) { this.customerNotes = customerNotes; }
    
    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
    
    public List<OrderLineResponse> getOrderLines() { return orderLines; }
    public void setOrderLines(List<OrderLineResponse> orderLines) { this.orderLines = orderLines; }
    
    public List<OrderModificationHistoryResponse> getModificationHistory() { return modificationHistory; }
    public void setModificationHistory(List<OrderModificationHistoryResponse> modificationHistory) { 
        this.modificationHistory = modificationHistory; 
    }

    // Classes internes pour les réponses
    public static class DeliveryAddressResponse {
        private String recipientName;
        private String streetAddress;
        private String city;
        private String postalCode;
        private String country;
        private String additionalInstructions;

        // Getters et Setters
        public String getRecipientName() { return recipientName; }
        public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
        
        public String getStreetAddress() { return streetAddress; }
        public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        
        public String getAdditionalInstructions() { return additionalInstructions; }
        public void setAdditionalInstructions(String additionalInstructions) { 
            this.additionalInstructions = additionalInstructions; 
        }
    }

    public static class BillingAddressResponse {
        private String billingName;
        private String streetAddress;
        private String city;
        private String postalCode;
        private String country;
        private String companyName;
        private String vatNumber;

        // Getters et Setters
        public String getBillingName() { return billingName; }
        public void setBillingName(String billingName) { this.billingName = billingName; }
        
        public String getStreetAddress() { return streetAddress; }
        public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        
        public String getVatNumber() { return vatNumber; }
        public void setVatNumber(String vatNumber) { this.vatNumber = vatNumber; }
    }

    public static class PaymentSummaryResponse {
        private BigDecimal totalAmount;
        private BigDecimal taxAmount;
        private BigDecimal shippingAmount;
        private BigDecimal discountAmount;
        private BigDecimal netAmount;
        private String currency;

        // Getters et Setters
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        
        public BigDecimal getTaxAmount() { return taxAmount; }
        public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
        
        public BigDecimal getShippingAmount() { return shippingAmount; }
        public void setShippingAmount(BigDecimal shippingAmount) { this.shippingAmount = shippingAmount; }
        
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        
        public BigDecimal getNetAmount() { return netAmount; }
        public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }

    public static class OrderLineResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productSku;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private String productImageUrl;
        private String productDescription;

        // Getters et Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public String getProductSku() { return productSku; }
        public void setProductSku(String productSku) { this.productSku = productSku; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        
        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
        
        public String getProductImageUrl() { return productImageUrl; }
        public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
        
        public String getProductDescription() { return productDescription; }
        public void setProductDescription(String productDescription) { this.productDescription = productDescription; }
    }

    public static class OrderModificationHistoryResponse {
        private Long id;
        private OrderStatus previousStatus;
        private OrderStatus newStatus;
        private String reason;
        private String modifiedBy;
        private LocalDateTime modifiedAt;
        private String additionalNotes;

        // Getters et Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public OrderStatus getPreviousStatus() { return previousStatus; }
        public void setPreviousStatus(OrderStatus previousStatus) { this.previousStatus = previousStatus; }
        
        public OrderStatus getNewStatus() { return newStatus; }
        public void setNewStatus(OrderStatus newStatus) { this.newStatus = newStatus; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        public String getModifiedBy() { return modifiedBy; }
        public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }
        
        public LocalDateTime getModifiedAt() { return modifiedAt; }
        public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }
        
        public String getAdditionalNotes() { return additionalNotes; }
        public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }
    }
}

