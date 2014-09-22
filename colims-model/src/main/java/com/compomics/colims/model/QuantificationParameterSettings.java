/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;

import javax.persistence.Entity;
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
@Table(name = "quant_parameter_settings")
@Entity
public class QuantificationParameterSettings extends DatabaseEntity {

    private static final long serialVersionUID = -2166573588583909556L;

    @Basic(optional = true)
    @Column(name = "label_count", nullable = true)
    private Integer labelCount;
    @Basic(optional = true)
    @Column(name = "minimum_ratio_count", nullable = true)
    private Integer minimumRatioCount;
    @Basic(optional = true)
    @Column(name = "error", nullable = true)
    private Double error;
    @Basic(optional = true)
    @Column(name = "include_modifications", nullable = true)
    private Boolean includeModifications;
    @Basic(optional = true)
    @ManyToOne
    @JoinColumn(name = "l_method_cv_id", referencedColumnName = "id", nullable = true)
    private QuantificationParamCvTerm method;
    @OneToMany(mappedBy = "quantificationParameterSettings")
    private List<QuantificationSettings> quantificationSettingses = new ArrayList<>();
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "quant_param_settings_has_reagent",
            joinColumns = {
        @JoinColumn(name = "l_quant_param_settings_id", referencedColumnName = "id")},
            inverseJoinColumns = {
        @JoinColumn(name = "l_quant_param_cv_term_id", referencedColumnName = "id")})
    private List<QuantificationParamCvTerm> reagents = new ArrayList<>();

    public Integer getLabelCount() {
        return labelCount;
    }

    public void setLabelCount(Integer labelCount) {
        this.labelCount = labelCount;
    }

    public Integer getMinimumRatioCount() {
        return minimumRatioCount;
    }

    public void setMinimumRatioCount(Integer minimumRatioCount) {
        this.minimumRatioCount = minimumRatioCount;
    }

    public Double getError() {
        return error;
    }

    public void setError(Double error) {
        this.error = error;
    }

    public Boolean isIncludeModifications() {
        return includeModifications;
    }

    public void setIncludeModifications(Boolean includeModifications) {
        this.includeModifications = includeModifications;
    }

    public List<QuantificationSettings> getQuantificationSettingses() {
        return quantificationSettingses;
    }

    public void setQuantificationSettingses(List<QuantificationSettings> quantificationSettingses) {
        this.quantificationSettingses = quantificationSettingses;
    }

    public QuantificationParamCvTerm getMethod() {
        return method;
    }

    public void setMethod(QuantificationParamCvTerm method) {
        this.method = method;
    }

    public List<QuantificationParamCvTerm> getReagents() {
        return reagents;
    }

    public void setReagents(List<QuantificationParamCvTerm> reagents) {
        this.reagents = reagents;
    }

}
