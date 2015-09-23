/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * Abstract parent class for modification entities.
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public abstract class AbstractModification extends DatabaseEntity {

    private static final long serialVersionUID = -1529392356700707358L;

    //@todo make mandatory?
    /**
     * The accession (UNIMOD, PSI-MOD).
     */
    @Basic(optional = true)
    @Column(name = "accession", nullable = true)
    protected String accession;
    /**
     * The modification name.
     */
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
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.accession);
        hash = 71 * hash + Objects.hashCode(this.name);
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
        final AbstractModification other = (AbstractModification) obj;
        if (!Objects.equals(this.accession, other.accession)) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }

}
