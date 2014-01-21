/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "institution")
@Entity
public class Institution extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotBlank(message = "Please insert an institution name")
    @Length(min = 5, max = 30, message = "Institution name must be between {min} and {max} characters")
    @Column(name = "name", nullable = false)
    private String name;
//    @NotBlank(message = "Please insert an institution abbreviation")
    @Basic(optional = false)
    @Length(min = 1, max = 10, message = "Institution abbreviation must be between {min} and {max} characters")
    @Column(name = "abbreviation", nullable = false)
    private String abbreviation;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a street name")
    @Length(min = 3, max = 20, message = "Street name must be between {min} and {max} characters")
    @Column(name = "street", nullable = false)
    private String street;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a street number")
    @Column(name = "number", nullable = false)
    private Integer number;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a city")
    @Length(min = 3, max = 30, message = "Institution city name must be between {min} and {max} characters")
    @Column(name = "city", nullable = false)
    private String city;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a postal code")
    @Column(name = "postal_code", nullable = false)
    private Integer postalCode;
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
}
