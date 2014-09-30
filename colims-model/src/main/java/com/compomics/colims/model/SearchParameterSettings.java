/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.enums.MassAccuracyType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_parameter_settings")
@Entity
public class SearchParameterSettings extends DatabaseEntity {

    private static final long serialVersionUID = -1065576089263244645L;

    /**
     * The type of search performed e.g. PMF, Tag searches, MS-MS.
     */
    @Basic(optional = true)
    @ManyToOne
    @JoinColumn(name = "l_search_type_cv_id", referencedColumnName = "id", nullable = true)
    private SearchParamCvParam searchType;
    /**
     * The cleavage enzyme.
     */
    @Basic(optional = true)
    @ManyToOne
    @JoinColumn(name = "l_search_param_enzyme_cv_id", referencedColumnName = "id", nullable = true)
    private SearchParamCvParam enzyme;
    /**
     * The number of missed cleavage sites allowed by the search.
     */
    @Column(name = "missed_cleavages", nullable = true)
    private Integer numberOfMissedCleavages;
    /**
     * The threshold(s) applied to determine that a result is significant.
     */
    @Basic(optional = true)
    @Column(name = "threshold", nullable = true)
    private Double threshold;
    /**
     * The precursor mass tolerance value.
     */
    @Basic(optional = true)
    @Column(name = "precursor_mass_tolerance", nullable = true)
    private Double precMassTolerance;
    /**
     * The precursor mass tolerance unit.
     */
    @Column(name = "precursor_mass_tolerance_unit", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private MassAccuracyType precMassToleranceUnit;
    /**
     * The fragment mass tolerance value.
     */
    @Basic(optional = true)
    @Column(name = "fragment_mass_tolerance", nullable = true)
    private Double fragMassTolerance;
    /**
     * The fragment mass tolerance unit.
     */
    @Basic(optional = true)
    @Column(name = "fragment_mass_tolerance_unit", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private MassAccuracyType fragMassToleranceUnit;
    /**
     * The lowest charge considered for the search.
     */
    @Basic(optional = true)
    @Column(name = "lower_charge", nullable = true)
    private Integer lowerCharge;
    /**
     * The highest charge considered for the search.
     */
    @Basic(optional = true)
    @Column(name = "upper_charge", nullable = true)
    private Integer upperCharge;
    @Basic(optional = true)
    @Column(name = "search_ion_type_1", nullable = true)
    private Integer firstSearchedIonType;
    @Basic(optional = true)
    @Column(name = "search_ion_type_2", nullable = true)
    private Integer secondSearchedIonType;
    @OneToMany(mappedBy = "searchParameterSettings")
    private List<SearchAndValidationSettings> searchAndValidationSettingses = new ArrayList<>();
    /**
     * The search parameters other than the modifications searched.
     */
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "search_param_set_has_add_cv_param",
            joinColumns = {
                @JoinColumn(name = "l_search_param_settings_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                @JoinColumn(name = "l_additional_cv_param_id", referencedColumnName = "id")})
    private List<ProtocolCvParam> additionalCvParams = new ArrayList<>();

    public SearchParamCvParam getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchParamCvParam searchType) {
        this.searchType = searchType;
    }

    public SearchParamCvParam getEnzyme() {
        return enzyme;
    }

    public void setEnzyme(SearchParamCvParam enzyme) {
        this.enzyme = enzyme;
    }

    public Integer getNumberOfMissedCleavages() {
        return numberOfMissedCleavages;
    }

    public void setNumberOfMissedCleavages(Integer numberOfMissedCleavages) {
        this.numberOfMissedCleavages = numberOfMissedCleavages;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public Double getPrecMassTolerance() {
        return precMassTolerance;
    }

    public void setPrecMassTolerance(Double precMassTolerance) {
        this.precMassTolerance = precMassTolerance;
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

    public List<SearchAndValidationSettings> getSearchAndValidationSettingses() {
        return searchAndValidationSettingses;
    }

    public void setSearchAndValidationSettingses(List<SearchAndValidationSettings> searchAndValidationSettingses) {
        this.searchAndValidationSettingses = searchAndValidationSettingses;
    }

    public List<ProtocolCvParam> getAdditionalCvParams() {
        return additionalCvParams;
    }

    public void setAdditionalCvParams(List<ProtocolCvParam> additionalCvParams) {
        this.additionalCvParams = additionalCvParams;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.searchType);
        hash = 41 * hash + Objects.hashCode(this.enzyme);
        hash = 41 * hash + Objects.hashCode(this.numberOfMissedCleavages);
        hash = 41 * hash + Objects.hashCode(this.threshold);
        hash = 41 * hash + Objects.hashCode(this.precMassTolerance);
        hash = 41 * hash + Objects.hashCode(this.precMassToleranceUnit);
        hash = 41 * hash + Objects.hashCode(this.fragMassTolerance);
        hash = 41 * hash + Objects.hashCode(this.fragMassToleranceUnit);
        hash = 41 * hash + Objects.hashCode(this.lowerCharge);
        hash = 41 * hash + Objects.hashCode(this.upperCharge);
        hash = 41 * hash + Objects.hashCode(this.firstSearchedIonType);
        hash = 41 * hash + Objects.hashCode(this.secondSearchedIonType);
        hash = 41 * hash + Objects.hashCode(this.additionalCvParams);
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
        final SearchParameterSettings other = (SearchParameterSettings) obj;
        if (!Objects.equals(this.searchType, other.searchType)) {
            return false;
        }
        if (!Objects.equals(this.enzyme, other.enzyme)) {
            return false;
        }
        if (!Objects.equals(this.numberOfMissedCleavages, other.numberOfMissedCleavages)) {
            return false;
        }
        if (!Objects.equals(this.threshold, other.threshold)) {
            return false;
        }
        if (!Objects.equals(this.precMassTolerance, other.precMassTolerance)) {
            return false;
        }
        if (this.precMassToleranceUnit != other.precMassToleranceUnit) {
            return false;
        }
        if (!Objects.equals(this.fragMassTolerance, other.fragMassTolerance)) {
            return false;
        }
        if (this.fragMassToleranceUnit != other.fragMassToleranceUnit) {
            return false;
        }
        if (!Objects.equals(this.lowerCharge, other.lowerCharge)) {
            return false;
        }
        if (!Objects.equals(this.upperCharge, other.upperCharge)) {
            return false;
        }
        if (!Objects.equals(this.firstSearchedIonType, other.firstSearchedIonType)) {
            return false;
        }
        if (!Objects.equals(this.secondSearchedIonType, other.secondSearchedIonType)) {
            return false;
        }
        if (!Objects.equals(this.additionalCvParams, other.additionalCvParams)) {
            return false;
        }
        return true;
    }

}
