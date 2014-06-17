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
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "quant_parameter_settings")
@Entity
public class QuantificationParameterSettings extends DatabaseEntity {

    private static final long serialVersionUID = 1L;

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
    @OneToMany(mappedBy = "quantificationParameterSettings")
    private List<QuantificationSettings> quantificationSettingses = new ArrayList<>();

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
    
}
