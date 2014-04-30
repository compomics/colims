/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
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
    @Column(name = "enzyme", nullable = true)
    private String enzyme;
    @Basic(optional = true)
    @Column(name = "missed_cleavages", nullable = true)
    private int numberOfMissedCleavages;
    @Basic(optional = true)
    @Column(name = "precursor_mass_tolerance", nullable = true)
    private double precMassTolerance;
    @Basic(optional = true)
    @Column(name = "lower_charge", nullable = true)
    private int lowerCharge;
    @Basic(optional = true)
    @Column(name = "upper_charge", nullable = true)
    private int upperCharge;
    @Basic(optional = true)
    @Column(name = "precursor_mass_tolerance_unit", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private MassAccuracyType precMassToleranceUnit;
    @Basic(optional = true)
    @Column(name = "fragment_mass_tolerance", nullable = true)
    private double fragMassTolerance;
    @Basic(optional = true)
    @Column(name = "fragment_mass_tolerance_unit", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private MassAccuracyType fragMassToleranceUnit;
    @Basic(optional = true)
    @Column(name = "search_ion_type_1", nullable = true)
    private Integer firstSearchedIonType;
    @Basic(optional = true)
    @Column(name = "search_ion_type_2", nullable = true)
    private Integer secondSearchedIonType;
    @Basic(optional = true)
    @Column(name = "evalue_cutoff", nullable = true)
    private double evalueCutoff;

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

    public int getLowerCharge() {
        return lowerCharge;
    }

    public void setLowerCharge(final int lowerCharge) {
        this.lowerCharge = lowerCharge;
    }

    public int getUpperCharge() {
        return upperCharge;
    }

    public void setUpperCharge(final int precursorUpperCharge) {
        this.upperCharge = precursorUpperCharge;
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

    public Integer getFirstSearchedIonType() {
        return firstSearchedIonType;
    }

    public void setFirstSearchedIonType(Integer firstSearchedIonType) {
        this.firstSearchedIonType = firstSearchedIonType;
    }

    public Integer getSecondSearchedIonType() {
        return secondSearchedIonType;
    }

    public void setSecondSearchedIonType(Integer secondSearchedIonType) {
        this.secondSearchedIonType = secondSearchedIonType;
    }

    public double getEvalueCutoff() {
        return evalueCutoff;
    }

    public void setEvalueCutoff(final double evalueCutoff) {
        this.evalueCutoff = evalueCutoff;
    }
    
    public List<SearchAndValidationSettings> getSearchAndValidationSettingses() {
        return searchAndValidationSettingses;
    }

    public void setSearchAndValidationSettingses(List<SearchAndValidationSettings> searchAndValidationSettingses) {
        this.searchAndValidationSettingses = searchAndValidationSettingses;
    }

}
