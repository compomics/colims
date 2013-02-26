/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.*;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "quantification_file")
@Entity
public class QuantMethodHasQuantEngine extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "l_quantification_group_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationGroup quantificationGroup;
    @JoinColumn(name = "l_quantification_engine_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationEngine quantificationEngine;
    @JoinColumn(name = "l_quant_param_settings_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationParameterSetting quantificationParamaterSetting;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuantificationGroup getQuantificationGroup() {
        return quantificationGroup;
    }

    public void setQuantificationGroup(QuantificationGroup quantificationGroup) {
        this.quantificationGroup = quantificationGroup;
    }

    public QuantificationEngine getQuantificationEngine() {
        return quantificationEngine;
    }

    public void setQuantificationEngine(QuantificationEngine quantificationEngine) {
        this.quantificationEngine = quantificationEngine;
    }

    public QuantificationParameterSetting getQuantificationParamaterSetting() {
        return quantificationParamaterSetting;
    }

    public void setQuantificationParamaterSetting(QuantificationParameterSetting quantificationParamaterSetting) {
        this.quantificationParamaterSetting = quantificationParamaterSetting;
    }
}
