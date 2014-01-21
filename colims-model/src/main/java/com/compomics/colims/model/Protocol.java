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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "protocol")
@Entity
public class Protocol extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a protocol name")
    @Length(min = 3, max = 30, message = "Name must be between {min} and {max} characters")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Basic(optional = true)
    @ManyToOne
    @JoinColumn(name = "l_reduction_cv_id", referencedColumnName = "id", nullable = false)
    private ProtocolCvTerm reduction;
    @Basic(optional = true)
    @ManyToOne
    @JoinColumn(name = "l_enzyme_cv_id", referencedColumnName = "id", nullable = false)
    private ProtocolCvTerm enzyme;
    @Basic(optional = true)
    @ManyToOne
    @JoinColumn(name = "l_cell_based_cv_id", referencedColumnName = "id", nullable = false)
    private ProtocolCvTerm cellBased;
    @OneToMany(mappedBy = "protocol")
    private List<Sample> samples = new ArrayList<>();
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "protocol_has_chemical_labeling",
            joinColumns = {
        @JoinColumn(name = "l_protocol_id", referencedColumnName = "id")},
            inverseJoinColumns = {
        @JoinColumn(name = "l_chemical_labeling_cv_term_id", referencedColumnName = "id")})
    private List<ProtocolCvTerm> chemicalLabels = new ArrayList<>();
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "protocol_has_other_cv_term",
            joinColumns = {
        @JoinColumn(name = "l_protocol_id", referencedColumnName = "id")},
            inverseJoinColumns = {
        @JoinColumn(name = "l_other_cv_term_id", referencedColumnName = "id")})
    private List<ProtocolCvTerm> otherCvTerms = new ArrayList<>();

    public Protocol() {
    }

    public Protocol(String name) {
        this.name = name;
    }

    public String getType() {
        return name;
    }

    public void setType(String type) {
        this.name = type;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProtocolCvTerm getReduction() {
        return reduction;
    }

    public void setReduction(ProtocolCvTerm reduction) {
        this.reduction = reduction;
    }

    public ProtocolCvTerm getEnzyme() {
        return enzyme;
    }

    public void setEnzyme(ProtocolCvTerm enzyme) {
        this.enzyme = enzyme;
    }

    public ProtocolCvTerm getCellBased() {
        return cellBased;
    }

    public void setCellBased(ProtocolCvTerm cellBased) {
        this.cellBased = cellBased;
    }

    public List<ProtocolCvTerm> getChemicalLabels() {
        return chemicalLabels;
    }

    public void setChemicalLabels(List<ProtocolCvTerm> chemicalLabels) {
        this.chemicalLabels = chemicalLabels;
    }

    public List<ProtocolCvTerm> getOtherCvTerms() {
        return otherCvTerms;
    }

    public void setOtherCvTerms(List<ProtocolCvTerm> otherCvTerms) {
        this.otherCvTerms = otherCvTerms;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.name);
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
        final Protocol other = (Protocol) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
