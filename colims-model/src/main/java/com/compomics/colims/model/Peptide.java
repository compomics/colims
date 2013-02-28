/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "peptide_sequence")
    private String sequence;
    @Column(name = "experimental_mass")
    private Double experimentalMass;
    @Column(name = "theoretical_mass")
    private Double theoreticalMass;
    @JoinColumn(name = "l_identification_file_id", referencedColumnName = "id")
    @ManyToOne
    private IdentificationFile identificationFile;
    @JoinColumn(name = "l_spectrum_id", referencedColumnName = "id")
    @ManyToOne
    private Spectrum spectrum;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "peptide")
    private List<PeptideHasModification> peptideHasModifications = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "peptide")
    private List<PeptideHasProtein> peptideHasProteins = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "peptide")
    private List<QuantificationGroupHasPeptide> quantificationGroupHasPeptides = new ArrayList<>();

    public Peptide() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
