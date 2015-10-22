package com.compomics.colims.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class represents a sample attachment in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "sample_binary_file")
@Entity
public class SampleBinaryFile extends BinaryFile {

    private static final long serialVersionUID = 1198531326938636719L;

    /**
     * The sample of the attachment.
     */
    @JoinColumn(name = "l_sample_id", referencedColumnName = "id")
    @ManyToOne
    private Sample sample;

    public Sample getSample() {
        return sample;
    }

    public void setSample(final Sample sample) {
        this.sample = sample;
    }

}
