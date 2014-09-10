/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "modification")
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Modification extends DatabaseEntity {

    private static final long serialVersionUID = 497141602900321901L;

    //@todo make mandatory?
    @Basic(optional = true)
    @Column(name = "accession", nullable = true)
    private String accession;
    @Basic(optional = true)
    @Column(name = "alternative_accession", nullable = true)
    private String alternativeAccession;
    @Basic(optional = false)
    @Column(name = "name", nullable = false)
    private String name;
    @Basic(optional = true)
    @Column(name = "monoisotopic_mass_shift")
    private Double monoIsotopicMassShift;
    @Basic(optional = true)
    @Column(name = "average_mass_shift")
    private Double averageMassShift;
    @Basic(optional = true)
    @Column(name = "monoisotopic_mass")
    private Double monoIsotopicMass;
    @Basic(optional = true)
    @Column(name = "average_mass")
    private Double averageMass;
    @OneToMany(mappedBy = "modification")
    private List<PeptideHasModification> peptideHasModifications = new ArrayList<>();

    public Modification() {
    }

    public Modification(String name) {
        this.name = name;
    }

    public Modification(String accession, String name) {
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

    public Double getMonoIsotopicMass() {
        return monoIsotopicMass;
    }

    public void setMonoIsotopicMass(Double monoIsotopicMass) {
        this.monoIsotopicMass = monoIsotopicMass;
    }

    public Double getAverageMass() {
        return averageMass;
    }

    public void setAverageMass(Double averageMass) {
        this.averageMass = averageMass;
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
        final Modification other = (Modification) obj;
        if (!Objects.equals(this.accession, other.accession)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
}
