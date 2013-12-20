/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "peptide")
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Peptide extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "peptide_sequence", nullable = false)
    private String sequence;
    @Basic(optional = true)
    @Column(name = "experimental_mass", nullable = true)
    private Double experimentalMass;
    @Basic(optional = true)
    @Column(name = "theoretical_mass", nullable = true)
    private Double theoreticalMass;
    @Basic(optional = true)
    @Column(name = "psm_prob", nullable = true)
    private Double psmProbability;
    @Basic(optional = true)
    @Column(name = "psm_post_error_prob", nullable = true)
    private Double psmPostErrorProbability;
    @JoinColumn(name = "l_identification_file_id", referencedColumnName = "id")
    @ManyToOne
    private IdentificationFile identificationFile;
    @JoinColumn(name = "l_spectrum_id", referencedColumnName = "id")
    @ManyToOne
    private Spectrum spectrum;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "peptide")
    private List<PeptideHasModification> peptideHasModifications = new ArrayList<>();
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "peptide")
    private List<PeptideHasProtein> peptideHasProteins = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "peptide")
    private List<QuantificationGroupHasPeptide> quantificationGroupHasPeptides = new ArrayList<>();

    public Peptide() {
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public Double getExperimentalMass() {
        return experimentalMass;
    }

    public void setExperimentalMass(Double experimentalMass) {
        this.experimentalMass = experimentalMass;
    }

    public Double getTheoreticalMass() {
        return theoreticalMass;
    }

    public void setTheoreticalMass(Double theoreticalMass) {
        this.theoreticalMass = theoreticalMass;
    }

    public IdentificationFile getIdentificationFile() {
        return identificationFile;
    }

    public void setIdentificationFile(IdentificationFile identificationFile) {
        this.identificationFile = identificationFile;
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public Double getPsmProbability() {
        return psmProbability;
    }

    public void setPsmProbability(Double psmProbability) {
        this.psmProbability = psmProbability;
    }

    public Double getPsmPostErrorProbability() {
        return psmPostErrorProbability;
    }

    public void setPsmPostErrorProbability(Double psmPostErrorProbability) {
        this.psmPostErrorProbability = psmPostErrorProbability;
    }

    public List<PeptideHasModification> getPeptideHasModifications() {
        return peptideHasModifications;
    }

    public void setPeptideHasModifications(List<PeptideHasModification> peptideHasModifications) {
        this.peptideHasModifications = peptideHasModifications;
    }

    public List<PeptideHasProtein> getPeptideHasProteins() {
        return peptideHasProteins;
    }

    public void setPeptideHasProteins(List<PeptideHasProtein> peptideHasProteins) {
        this.peptideHasProteins = peptideHasProteins;
    }

    public List<QuantificationGroupHasPeptide> getQuantificationGroupHasPeptides() {
        return quantificationGroupHasPeptides;
    }

    public void setQuantificationGroupHasPeptides(List<QuantificationGroupHasPeptide> quantificationGroupHasPeptides) {
        this.quantificationGroupHasPeptides = quantificationGroupHasPeptides;
    }

    public int getLength() {
        return sequence.length();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.sequence != null ? this.sequence.hashCode() : 0);
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
        final Peptide other = (Peptide) obj;
        if ((this.sequence == null) ? (other.sequence != null) : !this.sequence.equals(other.sequence)) {
            return false;
        }
        return true;
    }
}
