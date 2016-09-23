/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Abstract parent class for modification entities.
 *
 * @author Niels Hulstaert
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@MappedSuperclass
public abstract class AbstractModification extends DatabaseEntity {

    private static final long serialVersionUID = -1529392356700707358L;

    //@todo make mandatory?
    /**
     * The accession (UNIMOD, PSI-MOD).
     */
    @JsonProperty(value = "obo_id")
    @Basic(optional = true)
    @Column(name = "accession", nullable = true)
    protected String accession;
    /**
     * The modification name.
     */
    @JsonProperty(value = "label")
    @Basic(optional = false)
    @Column(name = "name", nullable = false)
    protected String name;
    /**
     * The Utilities name of the modification. This value is stored to facilitate the mapping to a Utilities PTM.
     */
    @Basic(optional = true)
    @Column(name = "utilities_name", nullable = true)
    protected String utilitiesName;
    /**
     * Atomic mass delta when assuming only the most common isotope of elements in Daltons.
     */
    @Basic(optional = true)
    @Column(name = "monoisotopic_mass_shift")
    protected Double monoIsotopicMassShift;
    /**
     * Atomic mass delta considering the natural distribution of isotopes in Dalton.
     */
    @Basic(optional = true)
    @Column(name = "average_mass_shift")
    protected Double averageMassShift;

    /**
     * No-arg constructor.
     */
    public AbstractModification() {
    }

    /**
     * Constructor.
     *
     * @param name the modification name
     */
    public AbstractModification(String name) {
        this.name = name;
    }

    /**
     * Constructor.
     *
     * @param accession the modification accession
     * @param name      the modification name
     */
    public AbstractModification(String accession, String name) {
        this.accession = accession;
        this.name = name;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUtilitiesName() {
        return utilitiesName;
    }

    public void setUtilitiesName(String utilitiesName) {
        this.utilitiesName = utilitiesName;
    }

    public Double getMonoIsotopicMassShift() {
        return monoIsotopicMassShift;
    }

    public void setMonoIsotopicMassShift(Double monoIsotopicMassShift) {
        this.monoIsotopicMassShift = monoIsotopicMassShift;
    }

    public Double getAverageMassShift() {
        return averageMassShift;
    }

    public void setAverageMassShift(Double averageMassShift) {
        this.averageMassShift = averageMassShift;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractModification that = (AbstractModification) o;

        if (accession != null ? !accession.equals(that.accession) : that.accession != null) return false;
        if (!name.equals(that.name)) return false;
        return !(utilitiesName != null ? !utilitiesName.equals(that.utilitiesName) : that.utilitiesName != null);

    }

    @Override
    public int hashCode() {
        int result = accession != null ? accession.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + (utilitiesName != null ? utilitiesName.hashCode() : 0);
        return result;
    }
}
