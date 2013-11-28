/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.util.experiment.identification.SearchParameters.PrecursorAccuracyType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_parameter_settings")
@Entity
public class SearchParameterSettings extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "l_s_and_val_set_has_s_eng_id", referencedColumnName = "id")
    @ManyToOne
    private SearchAndValSetHasSearchEngine searchAndValSetHasSearchEngine;
    @JoinColumn(name = "l_fasta_db_id", referencedColumnName = "id")
    @ManyToOne
    private FastaDb fastaDb;
    @Basic(optional = true)
    @Column(name = "enzyme")
    private String enzyme;
    @Basic(optional = false)
    @Column(name = "max_missed_cleavages")
    private int maxMissedCleavages;
    @Basic(optional = false)
    @Column(name = "precursor_mass_tolerance")
    private double precMassTolerance;
    @Basic(optional = false)
    @Column(name = "precursor_lower_charge")
    private int precursorLowerCharge;
    @Basic(optional = false)
    @Column(name = "precursor_upper_charge")
    private int precursorUpperCharge;
    @Basic(optional = false)
    @Column(name = "precursor_mass_tolerance_unit")
    @Enumerated(EnumType.ORDINAL)
    private PrecursorAccuracyType precMassToleranceUnit;
    @Basic(optional = false)
    @Column(name = "fragment_mass_tolerance")
    private double fragMassTolerance;
    @Basic(optional = false)
    @Column(name = "fragment_mass_tolerance_unit")
    @Enumerated(EnumType.ORDINAL)
    private PrecursorAccuracyType fragMassToleranceUnit;
    @Basic(optional = false)
    @Column(name = "fragment_ion_1_type")
    private int fragmentIon1Type;
    @Basic(optional = false)
    @Column(name = "fragment_ion_2_type")
    private int fragmentIon2Type;
    @Basic(optional = false)
    @Column(name = "evalue_cutoff")
    private double evalueCutoff;
    @Basic(optional = false)
    @Column(name = "hitlist_length")
    private int hitlistLength;

    public String getEnzyme() {
        return enzyme;
    }

    public void setEnzyme(String enzyme) {
        this.enzyme = enzyme;
    }

    public int getMaxMissedCleavages() {
        return maxMissedCleavages;
    }

    public void setMaxMissedCleavages(int maxMissedCleavages) {
        this.maxMissedCleavages = maxMissedCleavages;
    }

    public double getPrecMassTolerance() {
        return precMassTolerance;
    }

    public void setPrecMassTolerance(double precMassTolerance) {
        this.precMassTolerance = precMassTolerance;
    }

    public int getPrecursorLowerCharge() {
        return precursorLowerCharge;
    }

    public void setPrecursorLowerCharge(int precursorLowerCharge) {
        this.precursorLowerCharge = precursorLowerCharge;
    }

    public int getPrecursorUpperCharge() {
        return precursorUpperCharge;
    }

    public void setPrecursorUpperCharge(int precursorUpperCharge) {
        this.precursorUpperCharge = precursorUpperCharge;
    }

    public PrecursorAccuracyType getPrecMassToleranceUnit() {
        return precMassToleranceUnit;
    }

    public void setPrecMassToleranceUnit(PrecursorAccuracyType precMassToleranceUnit) {
        this.precMassToleranceUnit = precMassToleranceUnit;
    }

    public double getFragMassTolerance() {
        return fragMassTolerance;
    }

    public void setFragMassTolerance(double fragMassTolerance) {
        this.fragMassTolerance = fragMassTolerance;
    }

    public PrecursorAccuracyType getFragMassToleranceUnit() {
        return fragMassToleranceUnit;
    }

    public void setFragMassToleranceUnit(PrecursorAccuracyType fragMassToleranceUnit) {
        this.fragMassToleranceUnit = fragMassToleranceUnit;
    }

    public int getFragmentIon1Type() {
        return fragmentIon1Type;
    }

    public void setFragmentIon1Type(int fragmentIon1Type) {
        this.fragmentIon1Type = fragmentIon1Type;
    }

    public int getFragmentIon2Type() {
        return fragmentIon2Type;
    }

    public void setFragmentIon2Type(int fragmentIon2Type) {
        this.fragmentIon2Type = fragmentIon2Type;
    }

    public double getEvalueCutoff() {
        return evalueCutoff;
    }

    public void setEvalueCutoff(double evalueCutoff) {
        this.evalueCutoff = evalueCutoff;
    }

    public int getHitlistLength() {
        return hitlistLength;
    }

    public void setHitlistLength(int hitlistLength) {
        this.hitlistLength = hitlistLength;
    }

    public SearchAndValSetHasSearchEngine getSearchAndValSetHasSearchEngine() {
        return searchAndValSetHasSearchEngine;
    }

    public void setSearchAndValSetHasSearchEngine(SearchAndValSetHasSearchEngine searchAndValSetHasSearchEngine) {
        this.searchAndValSetHasSearchEngine = searchAndValSetHasSearchEngine;
    }

    public FastaDb getFastaDb() {
        return fastaDb;
    }

    public void setFastaDb(FastaDb fastaDb) {
        this.fastaDb = fastaDb;
    }

}
