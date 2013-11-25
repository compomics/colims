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
@Table(name = "quantification_method")
@Entity
public class QuantificationMethod extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @JoinColumn(name = "l_experiment_id", referencedColumnName = "id")
    @ManyToOne
    private Experiment experiment;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quantificationMethod")
    private List<QuantificationFile> quantificationFiles = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quantificationMethod")
    private List<QuantMethodHasQuantEngine> quantMethodHasQuantEngines = new ArrayList<>();

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public List<QuantificationFile> getQuantificationFiles() {
        return quantificationFiles;
    }

    public void setQuantificationFiles(List<QuantificationFile> quantificationFiles) {
        this.quantificationFiles = quantificationFiles;
    }

    public List<QuantMethodHasQuantEngine> getQuantMethodHasQuantEngines() {
        return quantMethodHasQuantEngines;
    }

    public void setQuantMethodHasQuantEngines(List<QuantMethodHasQuantEngine> quantMethodHasQuantEngines) {
        this.quantMethodHasQuantEngines = quantMethodHasQuantEngines;
    }
    
}
