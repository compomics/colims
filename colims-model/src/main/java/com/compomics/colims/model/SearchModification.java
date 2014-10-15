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

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_modification")
@Entity
public class SearchModification extends DatabaseEntity {

    private static final long serialVersionUID = -2832229647167375630L;

    /**
     * The PSI-MOD accession.
     */
    @Basic(optional = true)
    @Column(name = "accession", nullable = true)
    private String accession;
    /**
     * An alternative accession for this modification (UNIMOD accession for example).
     */
    @Basic(optional = true)
    @Column(name = "alternative_accession", nullable = true)
    private String alternativeAccession;
    /**
     * The modification name.
     */
    @Basic(optional = false)
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * Atomic mass delta when assuming only the most common isotope of elements
     * in Daltons.
     */
    @Basic(optional = true)
    @Column(name = "monoisotopic_mass_shift")
    private Double monoIsotopicMassShift;
    /**
     * Atomic mass delta considering the natural distribution of isotopes in
     * Daltons.
     */
    @Basic(optional = true)
    @Column(name = "average_mass_shift")
    private Double averageMassShift;
    @OneToMany(mappedBy = "modification")
    private List<PeptideHasModification> peptideHasModifications = new ArrayList<>();

    public SearchModification() {
    }

    public SearchModification(String name) {
        this.name = name;
    }

    public SearchModification(String accession, String name) {
        this.accession = accession;
        this.name = name;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getAlternativeAccession() {
        return alternativeAccession;
    }

    public void setAlternativeAccession(String alternativeAccession) {
        this.alternativeAccession = alternativeAccession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<PeptideHasModification> getPeptideHasModifications() {
        return peptideHasModifications;
    }

    public void setPeptideHasModifications(List<PeptideHasModification> peptideHasModifications) {
        this.peptideHasModifications = peptideHasModifications;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final SearchModification other = (SearchModification) obj;
        if (!Objects.equals(this.accession, other.accession)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

}
