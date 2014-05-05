/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.enums.QuantificationWeight;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 * @author Kenneth Verheggen
 */
@Table(name = "quantification")
@Entity
public class Quantification extends DatabaseEntity {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @Column(name = "intensity", nullable = false)
    private double intensity;
    @Basic(optional = false)
    @Column(name = "weight", nullable = false)
    private QuantificationWeight weight;
    @JoinColumn(name = "l_quantification_file_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationFile quantificationFile;
    @OneToMany(mappedBy = "quantification")
    private List<QuantificationGroup> quantificationGroups = new ArrayList<>();    

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(final double intensity) {
        this.intensity = intensity;
    }

    public void setWeight(final QuantificationWeight weight) {
        this.weight = weight;
    }

    public QuantificationWeight getWeight() {
        return weight;
    }  

    public QuantificationFile getQuantificationFile() {
        return quantificationFile;
    }

    public void setQuantificationFile(QuantificationFile quantificationFile) {
        this.quantificationFile = quantificationFile;
    }

    public List<QuantificationGroup> getQuantificationGroups() {
        return quantificationGroups;
    }

    public void setQuantificationGroups(List<QuantificationGroup> quantificationGroups) {
        this.quantificationGroups = quantificationGroups;
    }        

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.intensity) ^ (Double.doubleToLongBits(this.intensity) >>> 32));
        hash = 71 * hash + Objects.hashCode(this.weight);
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
        final Quantification other = (Quantification) obj;
        if (Double.doubleToLongBits(this.intensity) != Double.doubleToLongBits(other.intensity)) {
            return false;
        }
        if (this.weight != other.weight) {
            return false;
        }
        return true;
    }
        
}
