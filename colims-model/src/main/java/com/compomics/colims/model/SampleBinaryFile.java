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
@Table(name = "sample_binary_file")
@Entity
public class SampleBinaryFile extends AbstractBinaryFile {
    
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
