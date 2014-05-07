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
    private int labelCount;
    @Basic(optional = true)
    @Column(name = "minimum_ratio_count", nullable = true)
    private int minimumRatioCount;
    @Basic(optional = true)
    @Column(name = "error", nullable = true)
    private double error;
    @Basic(optional = true)
    @Column(name = "include_modifications", nullable = true)
    private boolean includeModifications;
    @OneToMany(mappedBy = "quantificationParameterSettings")
    private List<QuantificationSettings> quantificationSettingses = new ArrayList<>();

    public int getLabelCount() {
        return labelCount;
    }

    public void setLabelCount(int labelCount) {
        this.labelCount = labelCount;
    }

    public int getMinimumRatioCount() {
        return minimumRatioCount;
    }

    public void setMinimumRatioCount(int minimumRatioCount) {
        this.minimumRatioCount = minimumRatioCount;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public boolean isIncludeModifications() {
        return includeModifications;
    }

    public void setIncludeModifications(boolean includeModifications) {
        this.includeModifications = includeModifications;
    }    
    
    public List<QuantificationSettings> getQuantificationSettingses() {
        return quantificationSettingses;
    }

    public void setQuantificationSettingses(List<QuantificationSettings> quantificationSettingses) {
        this.quantificationSettingses = quantificationSettingses;
    }        
    
}
