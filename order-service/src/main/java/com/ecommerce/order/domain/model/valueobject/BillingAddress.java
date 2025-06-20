package com.ecommerce.order.domain.model.valueobject;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public class BillingAddress {
    
    @NotBlank(message = "Le nom de facturation est obligatoire")
    private final String billingName;
    
    @NotBlank(message = "L'adresse de facturation est obligatoire")
    private final String streetAddress;
    
    @NotBlank(message = "La ville est obligatoire")
    private final String city;
    
    @NotBlank(message = "Le code postal est obligatoire")
    private final String postalCode;
    
    @NotBlank(message = "Le pays est obligatoire")
    private final String country;
    
    private final String companyName;
    private final String vatNumber;

    public BillingAddress(String billingName, String streetAddress, String city, 
                         String postalCode, String country, String companyName, String vatNumber) {
        this.billingName = billingName;
        this.streetAddress = streetAddress;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.companyName = companyName;
        this.vatNumber = vatNumber;
    }

    public String getBillingName() {
        return billingName;
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

    public String getCompanyName() {
        return companyName;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillingAddress that = (BillingAddress) o;
        return Objects.equals(billingName, that.billingName) &&
               Objects.equals(streetAddress, that.streetAddress) &&
               Objects.equals(city, that.city) &&
               Objects.equals(postalCode, that.postalCode) &&
               Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billingName, streetAddress, city, postalCode, country);
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s %s, %s", 
                billingName, streetAddress, postalCode, city, country);
    }
}

