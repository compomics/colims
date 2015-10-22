package com.compomics.colims.model;

import com.compomics.colims.model.enums.QuantificationWeight;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels Hulstaert
 * @author Kenneth Verheggen
 */
@Table(name = "quantification")
@Entity
public class Quantification extends DatabaseEntity {

    private static final long serialVersionUID = -8721138574314561811L;

    @Basic(optional = false)
    @Column(name = "intensity", nullable = false)
    private Double intensity;
    @Basic(optional = false)
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "weight", nullable = false)
    private QuantificationWeight weight;
    @JoinColumn(name = "l_quantification_file_id", referencedColumnName = "id")
    @ManyToOne
    private QuantificationFile quantificationFile;
    @OneToMany(mappedBy = "quantification")
    private List<QuantificationGroup> quantificationGroups = new ArrayList<>();

    public Double getIntensity() {
        return intensity;
    }

    public void setIntensity(Double intensity) {
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

}
