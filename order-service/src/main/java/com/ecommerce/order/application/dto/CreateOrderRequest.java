package com.ecommerce.order.application.dto;

import com.ecommerce.order.domain.model.valueobject.BillingAddress;
import com.ecommerce.order.domain.model.valueobject.DeliveryAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateOrderRequest {
    
    @NotNull(message = "L'ID du client est obligatoire")
    private Long customerId;
    
    @NotNull(message = "L'adresse de livraison est obligatoire")
    @Valid
    private DeliveryAddressDto deliveryAddress;
    
    @NotNull(message = "L'adresse de facturation est obligatoire")
    @Valid
    private BillingAddressDto billingAddress;
    
    @NotEmpty(message = "Les lignes de commande sont obligatoires")
    @Valid
    private List<OrderLineDto> orderLines;
    
    private String customerNotes;

    // Constructeurs
    public CreateOrderRequest() {}

    public CreateOrderRequest(Long customerId, DeliveryAddressDto deliveryAddress, 
                             BillingAddressDto billingAddress, List<OrderLineDto> orderLines) {
        this.customerId = customerId;
        this.deliveryAddress = deliveryAddress;
        this.billingAddress = billingAddress;
        this.orderLines = orderLines;
    }

    // Getters et Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public DeliveryAddressDto getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddressDto deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public BillingAddressDto getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(BillingAddressDto billingAddress) {
        this.billingAddress = billingAddress;
    }

    public List<OrderLineDto> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLineDto> orderLines) {
        this.orderLines = orderLines;
    }

    public String getCustomerNotes() {
        return customerNotes;
    }

    public void setCustomerNotes(String customerNotes) {
        this.customerNotes = customerNotes;
    }

    // Classes internes pour les DTOs
    public static class DeliveryAddressDto {
        @NotNull(message = "Le nom du destinataire est obligatoire")
        private String recipientName;
        
        @NotNull(message = "L'adresse est obligatoire")
        private String streetAddress;
        
        @NotNull(message = "La ville est obligatoire")
        private String city;
        
        @NotNull(message = "Le code postal est obligatoire")
        private String postalCode;
        
        @NotNull(message = "Le pays est obligatoire")
        private String country;
        
        private String additionalInstructions;

        // Constructeurs
        public DeliveryAddressDto() {}

        public DeliveryAddressDto(String recipientName, String streetAddress, String city, 
                                 String postalCode, String country) {
            this.recipientName = recipientName;
            this.streetAddress = streetAddress;
            this.city = city;
            this.postalCode = postalCode;
            this.country = country;
        }

        // Méthode de conversion vers le value object du domaine
        public DeliveryAddress toDomainObject() {
            return new DeliveryAddress(recipientName, streetAddress, city, 
                                     postalCode, country, additionalInstructions);
        }

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

    public static class BillingAddressDto {
        @NotNull(message = "Le nom de facturation est obligatoire")
        private String billingName;
        
        @NotNull(message = "L'adresse de facturation est obligatoire")
        private String streetAddress;
        
        @NotNull(message = "La ville est obligatoire")
        private String city;
        
        @NotNull(message = "Le code postal est obligatoire")
        private String postalCode;
        
        @NotNull(message = "Le pays est obligatoire")
        private String country;
        
        private String companyName;
        private String vatNumber;

        // Constructeurs
        public BillingAddressDto() {}

        public BillingAddressDto(String billingName, String streetAddress, String city, 
                                String postalCode, String country) {
            this.billingName = billingName;
            this.streetAddress = streetAddress;
            this.city = city;
            this.postalCode = postalCode;
            this.country = country;
        }

        // Méthode de conversion vers le value object du domaine
        public BillingAddress toDomainObject() {
            return new BillingAddress(billingName, streetAddress, city, 
                                    postalCode, country, companyName, vatNumber);
        }

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

    public static class OrderLineDto {
        @NotNull(message = "L'ID du produit est obligatoire")
        private Long productId;
        
        @NotNull(message = "Le nom du produit est obligatoire")
        private String productName;
        
        @NotNull(message = "Le SKU du produit est obligatoire")
        private String productSku;
        
        @NotNull(message = "La quantité est obligatoire")
        private Integer quantity;
        
        @NotNull(message = "Le prix unitaire est obligatoire")
        private java.math.BigDecimal unitPrice;
        
        private String productImageUrl;
        private String productDescription;

        // Constructeurs
        public OrderLineDto() {}

        public OrderLineDto(Long productId, String productName, String productSku, 
                           Integer quantity, java.math.BigDecimal unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.productSku = productSku;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        // Getters et Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public String getProductSku() { return productSku; }
        public void setProductSku(String productSku) { this.productSku = productSku; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public java.math.BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(java.math.BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        
        public String getProductImageUrl() { return productImageUrl; }
        public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }
        
        public String getProductDescription() { return productDescription; }
        public void setProductDescription(String productDescription) { this.productDescription = productDescription; }
    }
}

