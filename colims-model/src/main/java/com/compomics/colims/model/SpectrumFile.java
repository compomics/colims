package com.compomics.colims.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This class represents a spectrum file entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "spectrum_file")
@Entity
public class SpectrumFile extends DatabaseEntity {

    private static final long serialVersionUID = -6234803421590111200L;

    /**
     * The gzipped MGF file as a byte array.
     */
    @Lob
    @Basic(optional = false)
    @Column(name = "content", nullable = false)
    private byte[] content;
    /**
     * The Spectrum instance of this spectrum file.
     */
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
