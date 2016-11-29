package com.compomics.colims.model;

import com.compomics.colims.model.enums.MassAccuracyType;
import com.compomics.colims.model.enums.ScoreType;
import com.compomics.colims.model.util.CompareUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a search parameters entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_parameters")
@Entity
public class SearchParameters extends DatabaseEntity {

    private static final long serialVersionUID = -1065576089263244645L;

    /**
     * The type of search performed e.g. PMF, Tag searches, MS-MS.
     */
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "l_search_type_cv_id", referencedColumnName = "id", nullable = true)
    private SearchCvParam searchType;
    /**
     * The cleavage enzyme(s), separated by semicolon.
     */
    @Column(name = "enzymes", nullable = true)
    private String enzymes;
    /**
     * The number of missed cleavage sites allowed by the search.
     */
    @Column(name = "missed_cleavages", nullable = true)
    private Integer numberOfMissedCleavages;
    /**
     * The target-decoy score strategy.
     */
    @Column(name = "score_type", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private ScoreType scoreType;
    /**
     * The PSM sequence-level threshold.
     */
    @Basic(optional = true)
    @Column(name = "psm_threshold", nullable = true)
    private Double psmThreshold;
    /**
     * The peptide sequence-level threshold.
     */
    @Basic(optional = true)
    @Column(name = "peptide_threshold", nullable = true)
    private Double peptideThreshold;
    /**
     * The protein sequence-level threshold.
     */
    @Basic(optional = true)
    @Column(name = "protein_threshold", nullable = true)
    private Double proteinThreshold;
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
    /**
     * The first ion type searched.
     */
    @Basic(optional = true)
    @Column(name = "search_ion_type_1", nullable = true)
    private Integer firstSearchedIonType;
    /**
     * The second ion type searched.
     */
    @Basic(optional = true)
    @Column(name = "search_ion_type_2", nullable = true)
    private Integer secondSearchedIonType;
    /**
     * The SearchParametersHasModification instances from the join table between the search parameters and search
     * modifications.
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "searchParameters", cascade = CascadeType.ALL)
    private List<SearchParametersHasModification> searchParametersHasModifications = new ArrayList<>();
    @OneToMany(mappedBy = "searchParameters")
    private List<SearchAndValidationSettings> searchAndValidationSettingses = new ArrayList<>();
    /**
     * The search parameters other than the modifications searched.
     */
    @ManyToMany
    @JoinTable(name = "search_params_has_other_cv_param",
            joinColumns = {
                    @JoinColumn(name = "l_search_params_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "l_other_search_cv_param_id", referencedColumnName = "id")})
    private List<SearchCvParam> additionalCvParams = new ArrayList<>();

    public SearchCvParam getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchCvParam searchType) {
        this.searchType = searchType;
    }

    public String getEnzymes() {
        return enzymes;
    }

    public void setEnzymes(String enzymes) {
        this.enzymes = enzymes;
    }

    public Integer getNumberOfMissedCleavages() {
        return numberOfMissedCleavages;
    }

    public void setNumberOfMissedCleavages(Integer numberOfMissedCleavages) {
        this.numberOfMissedCleavages = numberOfMissedCleavages;
    }

    public ScoreType getScoreType() {
        return scoreType;
    }

    public void setScoreType(ScoreType scoreType) {
        this.scoreType = scoreType;
    }

    public Double getPsmThreshold() {
        return psmThreshold;
    }

    public void setPsmThreshold(Double psmThreshold) {
        this.psmThreshold = psmThreshold;
    }

    public Double getPeptideThreshold() {
        return peptideThreshold;
    }

    public void setPeptideThreshold(Double peptideThreshold) {
        this.peptideThreshold = peptideThreshold;
    }

    public Double getProteinThreshold() {
        return proteinThreshold;
    }

    public void setProteinThreshold(Double proteinThreshold) {
        this.proteinThreshold = proteinThreshold;
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

    public List<SearchCvParam> getAdditionalCvParams() {
        return additionalCvParams;
    }

    public void setAdditionalCvParams(List<SearchCvParam> additionalCvParams) {
        this.additionalCvParams = additionalCvParams;
    }

    public List<SearchParametersHasModification> getSearchParametersHasModifications() {
        return searchParametersHasModifications;
    }

    public void setSearchParametersHasModifications(List<SearchParametersHasModification> searchParametersHasModifications) {
        this.searchParametersHasModifications = searchParametersHasModifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchParameters that = (SearchParameters) o;

        if (searchType != null ? !searchType.equals(that.searchType) : that.searchType != null) return false;
        if (enzymes != null ? !enzymes.equals(that.enzymes) : that.enzymes != null) return false;
        if (numberOfMissedCleavages != null ? !numberOfMissedCleavages.equals(that.numberOfMissedCleavages) : that.numberOfMissedCleavages != null)
            return false;
        if (scoreType != that.scoreType) return false;
        if (psmThreshold != null ? !CompareUtils.equals(psmThreshold, that.psmThreshold) : that.psmThreshold != null)
            return false;
        if (peptideThreshold != null ? !CompareUtils.equals(peptideThreshold, that.peptideThreshold) : that.peptideThreshold != null)
            return false;
        if (proteinThreshold != null ? !CompareUtils.equals(proteinThreshold, that.proteinThreshold) : that.proteinThreshold != null)
            return false;
        if (precMassTolerance != null ? !CompareUtils.equals(precMassTolerance, that.precMassTolerance) : that.precMassTolerance != null)
            return false;
        if (precMassToleranceUnit != that.precMassToleranceUnit) return false;
        if (fragMassTolerance != null ? !CompareUtils.equals(fragMassTolerance, that.fragMassTolerance) : that.fragMassTolerance != null)
            return false;
        if (fragMassToleranceUnit != that.fragMassToleranceUnit) return false;
        if (lowerCharge != null ? !lowerCharge.equals(that.lowerCharge) : that.lowerCharge != null) return false;
        if (upperCharge != null ? !upperCharge.equals(that.upperCharge) : that.upperCharge != null) return false;
        if (firstSearchedIonType != null ? !firstSearchedIonType.equals(that.firstSearchedIonType) : that.firstSearchedIonType != null)
            return false;
        return !(secondSearchedIonType != null ? !secondSearchedIonType.equals(that.secondSearchedIonType) : that.secondSearchedIonType != null);

    }

    @Override
    public int hashCode() {
        int result = searchType != null ? searchType.hashCode() : 0;
        result = 31 * result + (enzymes != null ? enzymes.hashCode() : 0);
        result = 31 * result + (numberOfMissedCleavages != null ? numberOfMissedCleavages.hashCode() : 0);
        result = 31 * result + (scoreType != null ? scoreType.hashCode() : 0);
        result = 31 * result + (psmThreshold != null ? psmThreshold.hashCode() : 0);
        result = 31 * result + (peptideThreshold != null ? peptideThreshold.hashCode() : 0);
        result = 31 * result + (proteinThreshold != null ? proteinThreshold.hashCode() : 0);
        result = 31 * result + (precMassTolerance != null ? precMassTolerance.hashCode() : 0);
        result = 31 * result + (precMassToleranceUnit != null ? precMassToleranceUnit.hashCode() : 0);
        result = 31 * result + (fragMassTolerance != null ? fragMassTolerance.hashCode() : 0);
        result = 31 * result + (fragMassToleranceUnit != null ? fragMassToleranceUnit.hashCode() : 0);
        result = 31 * result + (lowerCharge != null ? lowerCharge.hashCode() : 0);
        result = 31 * result + (upperCharge != null ? upperCharge.hashCode() : 0);
        result = 31 * result + (firstSearchedIonType != null ? firstSearchedIonType.hashCode() : 0);
        result = 31 * result + (secondSearchedIonType != null ? secondSearchedIonType.hashCode() : 0);
        return result;
    }
}
