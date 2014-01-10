/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "spectrum_file")
@Entity
public class SpectrumFile extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    //the gzipped mgf file
    @Lob
    @Basic(optional = false)
    @Column(name = "content", nullable = false)
    private byte[] content;
    @JoinColumn(name = "l_spectrum_id", referencedColumnName = "id")
    @ManyToOne
    private Spectrum spectrum;    

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(final Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(final byte[] content) {
        this.content = content;
    }
}
