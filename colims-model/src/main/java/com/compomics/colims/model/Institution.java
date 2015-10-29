package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;

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

    public String[] getAddress() {
        String[] address = new String[3];

        address[0] = getStreet() + " " + getNumber();
        address[1] = getPostalCode() + " " + getCity();
        address[2] = getCountry();

        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Institution that = (Institution) o;

        if (!name.equals(that.name)) return false;
        if (!abbreviation.equals(that.abbreviation)) return false;
        if (!street.equals(that.street)) return false;
        if (!number.equals(that.number)) return false;
        if (!city.equals(that.city)) return false;
        if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null) return false;
        return !(country != null ? !country.equals(that.country) : that.country != null);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + abbreviation.hashCode();
        result = 31 * result + street.hashCode();
        result = 31 * result + number.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name + " (" + abbreviation + ")";
    }

}
