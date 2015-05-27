package com.compomics.colims.model;

import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * This class represents an institution entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "institution")
@Entity
public class Institution extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 2423413625675018198L;

    /**
     * The institution name.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert an institution name.")
    @Length(min = 4, max = 30, message = "Institution name must be between {min} and {max} characters.")
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * The institution abbreviation.
     */
    @NotBlank(message = "Please insert an institution abbreviation.")
    @Basic(optional = false)
    @Length(min = 2, max = 10, message = "Institution abbreviation must be between {min} and {max} characters.")
    @Column(name = "abbreviation", nullable = false)
    private String abbreviation;
    /**
     * The street where the institution is located.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a street name.")
    @Length(min = 3, max = 20, message = "Street name must be between {min} and {max} characters.")
    @Column(name = "street", nullable = false)
    private String street;
    /**
     * The street number of the institution.
     */
    @Basic(optional = false)
    @Min(value = 1, message = "Number must be higher or equal to 1.")
    @Column(name = "number", nullable = false)
    private Integer number;
    /**
     * The city of the institution.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a city")
    @Length(min = 3, max = 30, message = "City name must be between {min} and {max} characters.")
    @Column(name = "city", nullable = false)
    private String city;
    /**
     * The postal code of the institution city.
     */
    @Basic(optional = true)
    @Min(value = 1, message = "Postal code must be higher or equal to 1.")
    @Column(name = "postal_code", nullable = true)
    private Integer postalCode;
    /**
     * The country where the institution is located.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a country")
    @Length(min = 3, max = 30, message = "Institution country name must be between {min} and {max} characters")
    @Column(name = "country", nullable = false)
    private String country;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(final String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(final Integer number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final Integer postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.abbreviation);
        hash = 29 * hash + Objects.hashCode(this.street);
        hash = 29 * hash + Objects.hashCode(this.number);
        hash = 29 * hash + Objects.hashCode(this.city);
        hash = 29 * hash + Objects.hashCode(this.country);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Institution other = (Institution) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.abbreviation, other.abbreviation)) {
            return false;
        }
        if (!Objects.equals(this.street, other.street)) {
            return false;
        }
        if (!Objects.equals(this.number, other.number)) {
            return false;
        }
        if (!Objects.equals(this.city, other.city)) {
            return false;
        }
        return Objects.equals(this.country, other.country);
    }

    @Override
    public String toString() {
        return name + " (" + abbreviation + ")";
    }

}
