package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "species_cv_term")
@Entity
public class SpeciesCvTerm extends CvTerm {
    
    @OneToMany(mappedBy = "speciesCvTerm")
    private List<Sample> samples = new ArrayList<>();

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }    
    
}
