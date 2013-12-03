/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;


import com.compomics.colims.model.enums.QuantificationWeight;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
/**
 *
 * @author Kenneth Verheggen
 */
@Table(name = "quantification")
@Entity
public class Quantification extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "l_quantification_group_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationGroup quantificationGroup;
    @JoinColumn(name = "l_spectrum_id", referencedColumnName = "id")
    @ManyToOne
    private Spectrum spectrum;
    @Basic(optional = true)
    @Column(name = "weight")
    private double intensity;
    @Basic(optional = true)
    @Column(name = "intensity")
    private QuantificationWeight weight;

    public QuantificationGroup getQuantificationGroup() {
        return quantificationGroup;
    }

    public void setQuantificationGroup(QuantificationGroup quantificationGroup) {
        this.quantificationGroup = quantificationGroup;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public void setWeight(QuantificationWeight weight) {
        this.weight = weight;
    }

    public QuantificationWeight getWeight() {
        return weight;
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }
}
