package com.ecommerce.order.domain.model.valueobject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public class DeliveryAddress {
    
    @NotBlank(message = "Le nom du destinataire est obligatoire")
    private final String recipientName;
    
    @NotBlank(message = "L'adresse est obligatoire")
    private final String streetAddress;
    
    @NotBlank(message = "La ville est obligatoire")
    private final String city;
    
    @NotBlank(message = "Le code postal est obligatoire")
    private final String postalCode;
    
    @NotBlank(message = "Le pays est obligatoire")
    private final String country;
    
    private final String additionalInstructions;

    public DeliveryAddress(String recipientName, String streetAddress, String city, 
                          String postalCode, String country, String additionalInstructions) {
        this.recipientName = recipientName;
        this.streetAddress = streetAddress;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.additionalInstructions = additionalInstructions;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public String getAdditionalInstructions() {
        return additionalInstructions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryAddress that = (DeliveryAddress) o;
        return Objects.equals(recipientName, that.recipientName) &&
               Objects.equals(streetAddress, that.streetAddress) &&
               Objects.equals(city, that.city) &&
               Objects.equals(postalCode, that.postalCode) &&
               Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipientName, streetAddress, city, postalCode, country);
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s %s, %s", 
                recipientName, streetAddress, postalCode, city, country);
    }
}

