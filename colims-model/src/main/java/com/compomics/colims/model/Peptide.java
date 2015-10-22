package com.compomics.colims.model;

import com.compomics.colims.model.comparator.PeptideHasModificationLocationComparator;
import com.compomics.colims.model.util.CompareUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a peptide entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "peptide")
@Entity
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Peptide extends DatabaseEntity {

    private static final long serialVersionUID = -7678950773201086394L;

    /**
     * The peptide sequence string.
     */
    @Basic(optional = false)
    @Column(name = "peptide_sequence", nullable = false)
    private String sequence;
    /**
     * The theoretical mass value.
     */
    @Basic(optional = true)
    @Column(name = "theoretical_mass", nullable = true)
    private Double theoreticalMass;
    /**
     * The charge assigned by the search engine.
     */
    @Basic(optional = true)
    @Column(name = "charge", nullable = true)
    private Integer charge;
    /**
     * The peptide-to-spectrum probability score.
     */
    @Basic(optional = true)
    @Column(name = "psm_prob", nullable = true)
    private Double psmProbability;
    /**
     * The peptide-to-spectrum posterior error probability score.
     */
    @Basic(optional = true)
    @Column(name = "psm_post_error_prob", nullable = true)
    private Double psmPostErrorProbability;
    /**
     * The IdentificationFile instance that identified this peptide-to-spectrum match.
     */
    @JoinColumn(name = "l_identification_file_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private IdentificationFile identificationFile;
    /**
     * The spectrum identified by this peptide.
     */
    @JoinColumn(name = "l_spectrum_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Spectrum spectrum;
    /**
     * The PeptideHasModification instances from the join table between the peptide and modification tables.
     */
    @OneToMany(mappedBy = "peptide")
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<PeptideHasModification> peptideHasModifications = new ArrayList<>();
    /**
     * The PeptideHasProteinGroup instances from the join table between the peptide and protein group tables.
     */
    @OneToMany(mappedBy = "peptide")
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<PeptideHasProteinGroup> peptideHasProteinGroups = new ArrayList<>();
    @OneToMany(mappedBy = "peptide")
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<QuantificationGroup> quantificationGroups = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public Peptide() {
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public Double getTheoreticalMass() {
        return theoreticalMass;
    }

    public void setTheoreticalMass(Double theoreticalMass) {
        this.theoreticalMass = theoreticalMass;
    }

    public Integer getCharge() {
        return charge;
    }

    public void setCharge(Integer charge) {
        this.charge = charge;
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

    public List<PeptideHasProteinGroup> getPeptideHasProteinGroups() {
        return peptideHasProteinGroups;
    }

    public void setPeptideHasProteinGroups(List<PeptideHasProteinGroup> peptideHasProteinGroups) {
        this.peptideHasProteinGroups = peptideHasProteinGroups;
    }

    public List<QuantificationGroup> getQuantificationGroups() {
        return quantificationGroups;
    }

    public void setQuantificationGroups(List<QuantificationGroup> quantificationGroups) {
        this.quantificationGroups = quantificationGroups;
    }

    /**
     * Get the peptide sequence length.
     *
     * @return the sequence length
     */
    public int getLength() {
        return sequence.length();
    }

    /**
     * This method checks if the given peptide has the same sequence, scores and modification(s) (on the same
     * position(s)). Note that charge is not considered in this comparison.
     *
     * @param peptide the given Peptide instance
     * @return true if the peptide has the same modifications
     */
    public boolean equalsWithoutCharge(Peptide peptide) {
        if (!sequence.equals(peptide.sequence)) return false;
        if (theoreticalMass != null ? !CompareUtils.equals(theoreticalMass, peptide.theoreticalMass) : peptide.theoreticalMass != null)
            return false;
        if (psmProbability != null ? !CompareUtils.equals(psmProbability, peptide.psmProbability) : peptide.psmProbability != null)
            return false;
        if (psmPostErrorProbability != null ? !CompareUtils.equals(psmPostErrorProbability, peptide.psmPostErrorProbability) : peptide.psmPostErrorProbability != null) {
            return false;
        }
        if (peptideHasModifications.size() != peptide.peptideHasModifications.size()) {
            return false;
        }
        //sort the lists of PeptideHasModification instances
        PeptideHasModificationLocationComparator locationComparator = new PeptideHasModificationLocationComparator();
        Collections.sort(peptideHasModifications, locationComparator);
        Collections.sort(peptide.peptideHasModifications, locationComparator);
        //compare on location and modification
        for (int i = 0; i < peptideHasModifications.size(); i++) {
            if (!peptideHasModifications.get(i).equals(peptide.getPeptideHasModifications().get(i))) {
                return false;
            }
        }
        return true;
    }
}
