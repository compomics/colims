/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.util.experiment.identification.SearchParameters.MassAccuracyType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_parameter_settings")
@Entity
public class SearchParameterSettings extends DatabaseEntity {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "searchParameterSettings")
    private List<SearchAndValidationSettings> searchAndValidationSettingses = new ArrayList<>();
    @Basic(optional = true)
    @Column(name = "enzyme")
    private String enzyme;
    @Basic(optional = true)
    @Column(name = "missed_cleavages")
    private int numberOfMissedCleavages;
    @Basic(optional = true)
    @Column(name = "precursor_mass_tolerance")
    private double precMassTolerance;
    @Basic(optional = true)
    @Column(name = "precursor_lower_charge")
    private int precursorLowerCharge;
    @Basic(optional = true)
    @Column(name = "precursor_upper_charge")
    private int precursorUpperCharge;
    @Basic(optional = true)
    @Column(name = "precursor_mass_tolerance_unit")
    @Enumerated(EnumType.ORDINAL)
    private MassAccuracyType precMassToleranceUnit;
    @Basic(optional = true)
    @Column(name = "fragment_mass_tolerance")
    private double fragMassTolerance;
    @Basic(optional = true)
    @Column(name = "fragment_mass_tolerance_unit")
    @Enumerated(EnumType.ORDINAL)
    private MassAccuracyType fragMassToleranceUnit;
    @Basic(optional = true)
    @Column(name = "fragment_ion_1_type")
    private int fragmentIon1Type;
    @Basic(optional = true)
    @Column(name = "fragment_ion_2_type")
    private int fragmentIon2Type;
    @Basic(optional = true)
    @Column(name = "evalue_cutoff")
    private double evalueCutoff;
    @Basic(optional = true)
    @Column(name = "hitlist_length")
    private int hitlistLength;

    public String getEnzyme() {
        return enzyme;
    }

    public void setEnzyme(final String enzyme) {
        this.enzyme = enzyme;
    }

    public int getNumberOfMissedCleavages() {
        return numberOfMissedCleavages;
    }

    public void setNumberOfMissedCleavages(int numberOfMissedCleavages) {
        this.numberOfMissedCleavages = numberOfMissedCleavages;
    }    

    public double getPrecMassTolerance() {
        return precMassTolerance;
    }

    public void setPrecMassTolerance(final double precMassTolerance) {
        this.precMassTolerance = precMassTolerance;
    }

    public int getPrecursorLowerCharge() {
        return precursorLowerCharge;
    }

    public void setPrecursorLowerCharge(final int precursorLowerCharge) {
        this.precursorLowerCharge = precursorLowerCharge;
    }

    public int getPrecursorUpperCharge() {
        return precursorUpperCharge;
    }

    public void setPrecursorUpperCharge(final int precursorUpperCharge) {
        this.precursorUpperCharge = precursorUpperCharge;
    }

    public MassAccuracyType getPrecMassToleranceUnit() {
        return precMassToleranceUnit;
    }

    public void setPrecMassToleranceUnit(final MassAccuracyType precMassToleranceUnit) {
        this.precMassToleranceUnit = precMassToleranceUnit;
    }

    public double getFragMassTolerance() {
        return fragMassTolerance;
    }

    public void setFragMassTolerance(final double fragMassTolerance) {
        this.fragMassTolerance = fragMassTolerance;
    }

    public MassAccuracyType getFragMassToleranceUnit() {
        return fragMassToleranceUnit;
    }

    public void setFragMassToleranceUnit(final MassAccuracyType fragMassToleranceUnit) {
        this.fragMassToleranceUnit = fragMassToleranceUnit;
    }

    public int getFragmentIon1Type() {
        return fragmentIon1Type;
    }

    public void setFragmentIon1Type(final int fragmentIon1Type) {
        this.fragmentIon1Type = fragmentIon1Type;
    }

    public int getFragmentIon2Type() {
        return fragmentIon2Type;
    }

    public void setFragmentIon2Type(final int fragmentIon2Type) {
        this.fragmentIon2Type = fragmentIon2Type;
    }

    public double getEvalueCutoff() {
        return evalueCutoff;
    }

    public void setEvalueCutoff(final double evalueCutoff) {
        this.evalueCutoff = evalueCutoff;
    }

    public int getHitlistLength() {
        return hitlistLength;
    }

    public void setHitlistLength(final int hitlistLength) {
        this.hitlistLength = hitlistLength;
    }

    public List<SearchAndValidationSettings> getSearchAndValidationSettingses() {
        return searchAndValidationSettingses;
    }

    public void setSearchAndValidationSettingses(List<SearchAndValidationSettings> searchAndValidationSettingses) {
        this.searchAndValidationSettingses = searchAndValidationSettingses;
    }

}
