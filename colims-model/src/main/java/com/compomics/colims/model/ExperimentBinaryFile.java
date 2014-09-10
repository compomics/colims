/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "experiment_binary_file")
@Entity
public class ExperimentBinaryFile extends BinaryFile {

    private static final long serialVersionUID = -629806717593264864L;
    
    @JoinColumn(name = "l_experiment_id", referencedColumnName = "id")
    @ManyToOne
    private Experiment experiment;

    public ExperimentBinaryFile() {
    }

    public ExperimentBinaryFile(final byte[] content) {
        super(content);
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(final Experiment experiment) {
        this.experiment = experiment;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.experiment);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExperimentBinaryFile other = (ExperimentBinaryFile) obj;
        if (!Objects.equals(this.experiment, other.experiment)) {
            return false;
        }
        return true;
    }            
        
}
