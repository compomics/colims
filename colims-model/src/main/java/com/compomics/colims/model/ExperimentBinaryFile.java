package com.compomics.colims.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class represents an experiment attachment in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "experiment_binary_file")
@Entity
public class ExperimentBinaryFile extends BinaryFile {

    private static final long serialVersionUID = -629806717593264864L;

    /**
     * The experiment of the attachment.
     */
    @JoinColumn(name = "l_experiment_id", referencedColumnName = "id")
    @ManyToOne
    private Experiment experiment;

    /**
     * No-arg constructor.
     */
    public ExperimentBinaryFile() {
    }

    /**
     * Constructor.
     *
     * @param content the content as a byte array
     */
    public ExperimentBinaryFile(final byte[] content) {
        super(content);
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(final Experiment experiment) {
        this.experiment = experiment;
    }

}
