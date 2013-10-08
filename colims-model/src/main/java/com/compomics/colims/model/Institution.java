/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "institution")
@Entity
public class Institution extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
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
    @Length(min = 2, max = 20, message = "Street name must be between {min} and {max} characters")
    @Column(name = "street", nullable = false)
    private String street;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a street number")
    @Column(name = "number", nullable = false)
    private Integer number;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a city")
    @Length(min = 2, max = 30, message = "Institution city name must be between {min} and {max} characters")
    @Column(name = "city", nullable = false)
    private String city;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a postal code")
    @Column(name = "postal_code", nullable = false)
    private Integer postalCode;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a country")
    @Length(min = 2, max = 30, message = "Institution country name must be between {min} and {max} characters")
    @Column(name = "country", nullable = false)
    private String country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
