/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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

    @OneToMany(mappedBy = "quantificationParameterSettings")
    private List<QuantificationSettings> quantificationSettingses = new ArrayList<>();

    public List<QuantificationSettings> getQuantificationSettingses() {
        return quantificationSettingses;
    }

    public void setQuantificationSettingses(List<QuantificationSettings> quantificationSettingses) {
        this.quantificationSettingses = quantificationSettingses;
    }        
    
}
