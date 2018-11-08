package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

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
    @Length(min = 2, max = 50, message = "Institution name must be between {min} and {max} characters.")
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * The institution abbreviation.
     */
    @NotBlank(message = "Please insert an institution abbreviation.")
    @Basic(optional = false)
    @Length(min = 2, max = 15, message = "Institution abbreviation must be between {min} and {max} characters.")
    @Column(name = "abbreviation", nullable = false)
    private String abbreviation;
    /**
     * The street where the institution is located.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a street name.")
    @Length(min = 2, max = 50, message = "Street name must be between {min} and {max} characters.")
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
    @Length(min = 2, max = 50, message = "City name must be between {min} and {max} characters.")
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
    @Length(min = 2, max = 50, message = "Institution country name must be between {min} and {max} characters")
    @Column(name = "country", nullable = false)
    private String country;
    /**
     * The institution email.
     */
    @Basic(optional = true)
    @Column(name = "email", nullable = true)
    private String email;
    /**
     * The institution URL.
     */
    @Basic(optional = true)
    @Column(name = "url", nullable = true)
    private String url;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the address as a String array.
     *
     * @return the address array
     */
    public String[] getAddress() {
        String[] address = new String[3];

        address[0] = getStreet() + " " + getNumber();
        address[1] = getPostalCode() + " " + getCity();
        address[2] = getCountry();

        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Institution that = (Institution) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (abbreviation != null ? !abbreviation.equals(that.abbreviation) : that.abbreviation != null) {
            return false;
        }
        if (street != null ? !street.equals(that.street) : that.street != null) {
            return false;
        }
        if (number != null ? !number.equals(that.number) : that.number != null) {
            return false;
        }
        if (city != null ? !city.equals(that.city) : that.city != null) {
            return false;
        }
        if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null) {
            return false;
        }
        if (email != null ? !email.equals(that.email) : that.email != null) {
            return false;
        }
        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        return !(country != null ? !country.equals(that.country) : that.country != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (abbreviation != null ? abbreviation.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name + " (" + abbreviation + ")";
    }

}
