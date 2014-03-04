/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "quant_method_has_quant_engine")
@Entity
public class QuantMethodHasQuantEngine extends DatabaseEntity {

    private static final long serialVersionUID = 1L;
    @JoinColumn(name = "l_quantification_method_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationMethod quantificationMethod;
    @JoinColumn(name = "l_quantification_engine_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationEngine quantificationEngine;
    @JoinColumn(name = "l_quant_param_settings_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationParameterSetting quantificationParameterSetting;

    public QuantificationMethod getQuantificationMethod() {
        return quantificationMethod;
    }

    public void setQuantificationMethod(final QuantificationMethod quantificationMethod) {
        this.quantificationMethod = quantificationMethod;
    }

    public QuantificationEngine getQuantificationEngine() {
        return quantificationEngine;
    }

    public void setQuantificationEngine(final QuantificationEngine quantificationEngine) {
        this.quantificationEngine = quantificationEngine;
    }

    public QuantificationParameterSetting getQuantificationParameterSetting() {
        return quantificationParameterSetting;
    }

    public void setQuantificationParameterSetting(final QuantificationParameterSetting quantificationParameterSetting) {
        this.quantificationParameterSetting = quantificationParameterSetting;
    }
}
