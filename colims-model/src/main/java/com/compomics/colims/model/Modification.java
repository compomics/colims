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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "modification")
@Entity
public class Modification extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    //@todo make mandatory?
    @Basic(optional = true)
    @Column(name = "accession", nullable = true)
    private String accession;
    @Basic(optional = false)
    @Column(name = "name", nullable = false)
    private String name;
    @Basic(optional = true)
    @Column(name = "monoisotopic_mass_shift")
    private double monoIsotopicMassShift;
    @Basic(optional = true)
    @Column(name = "average_mass_shift")
    private double averageMassShift;
    @Basic(optional = true)
    @Column(name = "monoisotopic_mass")
    private double monoIsotopicMass;
    @Basic(optional = true)
    @Column(name = "average_mass")
    private double averageMass;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public double getMonoIsotopicMassShift() {
        return monoIsotopicMassShift;
    }

    public void setMonoIsotopicMassShift(double monoIsotopicMassShift) {
        this.monoIsotopicMassShift = monoIsotopicMassShift;
    }

    public double getAverageMassShift() {
        return averageMassShift;
    }

    public void setAverageMassShift(double averageMassShift) {
        this.averageMassShift = averageMassShift;
    }

    public double getMonoIsotopicMass() {
        return monoIsotopicMass;
    }

    public void setMonoIsotopicMass(double monoIsotopicMass) {
        this.monoIsotopicMass = monoIsotopicMass;
    }

    public double getAverageMass() {
        return averageMass;
    }

    public void setAverageMass(double averageMass) {
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
