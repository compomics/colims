/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.enums.MassAccuracyType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_parameter_settings")
@Entity
public class SearchParameterSettings extends DatabaseEntity {

    private static final long serialVersionUID = -1065576089263244645L;

    @Basic(optional = true)
    @Column(name = "enzyme", nullable = true)
    private String enzyme;
    @Basic(optional = true)
    @Column(name = "missed_cleavages", nullable = true)
    private Integer numberOfMissedCleavages;
    @Basic(optional = true)
    @Column(name = "precursor_mass_tolerance", nullable = true)
    private Double precMassTolerance;
    @Basic(optional = true)
    @Column(name = "lower_charge", nullable = true)
    private Integer lowerCharge;
    @Basic(optional = true)
    @Column(name = "upper_charge", nullable = true)
    private Integer upperCharge;
    @Basic(optional = true)
    @Column(name = "precursor_mass_tolerance_unit", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private MassAccuracyType precMassToleranceUnit;
    @Basic(optional = true)
    @Column(name = "fragment_mass_tolerance", nullable = true)
    private Double fragMassTolerance;
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
    private Double evalueCutoff;
    /**
     * The threshold(s) applied to determine that a result is significant.
     */
    @Basic(optional = true)
    @ManyToOne
    @JoinColumn(name = "l_threshold_cv_id", referencedColumnName = "id", nullable = true)
    private SearchParamCvParam threshold;
    @OneToMany(mappedBy = "searchParameterSettings")
    private List<SearchAndValidationSettings> searchAndValidationSettingses = new ArrayList<>();

    public String getEnzyme() {
        return enzyme;
    }

    public void setEnzyme(String enzyme) {
        this.enzyme = enzyme;
    }

    public Integer getNumberOfMissedCleavages() {
        return numberOfMissedCleavages;
    }

    public void setNumberOfMissedCleavages(Integer numberOfMissedCleavages) {
        this.numberOfMissedCleavages = numberOfMissedCleavages;
    }

    public Double getPrecMassTolerance() {
        return precMassTolerance;
    }

    public void setPrecMassTolerance(Double precMassTolerance) {
        this.precMassTolerance = precMassTolerance;
    }

    public Integer getLowerCharge() {
        return lowerCharge;
    }

    public void setLowerCharge(Integer lowerCharge) {
        this.lowerCharge = lowerCharge;
    }

    public Integer getUpperCharge() {
        return upperCharge;
    }

    public void setUpperCharge(Integer upperCharge) {
        this.upperCharge = upperCharge;
    }

    public MassAccuracyType getPrecMassToleranceUnit() {
        return precMassToleranceUnit;
    }

    public void setPrecMassToleranceUnit(MassAccuracyType precMassToleranceUnit) {
        this.precMassToleranceUnit = precMassToleranceUnit;
    }

    public Double getFragMassTolerance() {
        return fragMassTolerance;
    }

    public void setFragMassTolerance(Double fragMassTolerance) {
        this.fragMassTolerance = fragMassTolerance;
    }

    public MassAccuracyType getFragMassToleranceUnit() {
        return fragMassToleranceUnit;
    }

    public void setFragMassToleranceUnit(MassAccuracyType fragMassToleranceUnit) {
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

    public Double getEvalueCutoff() {
        return evalueCutoff;
    }

    public void setEvalueCutoff(Double evalueCutoff) {
        this.evalueCutoff = evalueCutoff;
    }

    public List<SearchAndValidationSettings> getSearchAndValidationSettingses() {
        return searchAndValidationSettingses;
    }

    public void setSearchAndValidationSettingses(List<SearchAndValidationSettings> searchAndValidationSettingses) {
        this.searchAndValidationSettingses = searchAndValidationSettingses;
    }

}
