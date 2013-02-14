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
@Table(name = "experiment_param")
@Entity
public class ExperimentParam extends AbstractParamEntity {
    
    private static final long serialVersionUID = 1L;
    
    @JoinColumn(name = "l_experiment_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Experiment experiment;

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }        
    
}
