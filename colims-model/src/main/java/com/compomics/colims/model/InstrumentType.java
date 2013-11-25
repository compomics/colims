/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "instrument_type")
@Entity
public class InstrumentType extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;
  
    @Basic(optional = false)
    @NotBlank(message = "Please insert an instrument type name")
    @Length(min = 2, max = 30, message = "Type name must be between {min} and {max} characters")
    @Column(name = "name", nullable = false)
    private String name; 
    @Basic(optional = true)
    @Length(max = 500, message = "Description must be less than {max} characters")
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "instrumentType")
    private List<Instrument> instruments = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }       

    public List<Instrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<Instrument> instruments) {
        this.instruments = instruments;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.name);
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
        final InstrumentType other = (InstrumentType) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }        
         
}
