/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "quantification_settings")
@Entity
public class QuantificationSettings extends DatabaseEntity {
    
    private static final long serialVersionUID = 1L;
        
    @JoinColumn(name = "l_experiment_id", referencedColumnName = "id")
    @ManyToOne
    private Experiment experiment;
    @JoinColumn(name = "l_quant_engine_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationEngine quantificationEngine;   
    @JoinColumn(name = "l_quant_param_settings_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationParameterSettings quantificationParameterSettings;    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quantificationSettings")
    private List<QuantificationFile> quantificationFiles = new ArrayList<>();    

    public List<QuantificationFile> getQuantificationFiles() {
        return quantificationFiles;
    }

    public void setQuantificationFiles(List<QuantificationFile> quantificationFiles) {
        this.quantificationFiles = quantificationFiles;
    }    

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public QuantificationEngine getQuantificationEngine() {
        return quantificationEngine;
    }

    public void setQuantificationEngine(QuantificationEngine quantificationEngine) {
        this.quantificationEngine = quantificationEngine;
    }

    public QuantificationParameterSettings getQuantificationParameterSettings() {
        return quantificationParameterSettings;
    }

    public void setQuantificationParameterSettings(QuantificationParameterSettings quantificationParameterSettings) {
        this.quantificationParameterSettings = quantificationParameterSettings;
    }    
    
}
