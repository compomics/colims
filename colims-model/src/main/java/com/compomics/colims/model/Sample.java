/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "sample")
@Entity
public class Sample extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = true)
    @Column(name = "accession")
    private String accession;
    @Basic(optional = true)
    @Column(name = "name")
    private String name;
    @ManyToOne 
    @JoinColumn(name = "l_species_cv_term_id", referencedColumnName = "id") 
    private SpeciesCvTerm speciesCvTerm;
    @JoinColumn(name = "l_experiment_id", referencedColumnName = "id")
    @ManyToOne
    private Experiment experiment;
    @ManyToMany
    @JoinTable(name = "sample_has_material",
    joinColumns = {
        @JoinColumn(name = "l_sample_id", referencedColumnName = "id")},
    inverseJoinColumns = {
        @JoinColumn(name = "l_material_id", referencedColumnName = "id")})    
    private List<Material> materials = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sample")
    private List<AnalyticalRun> analyticalRuns = new ArrayList<>();

    public Sample() {
    }

    public Sample(String accession) {
        this.accession = accession;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }    

    public List<AnalyticalRun> getAnalyticalRuns() {
        return analyticalRuns;
    }

    public void setAnalyticalRuns(List<AnalyticalRun> analyticalRuns) {
        this.analyticalRuns = analyticalRuns;
    }

    public SpeciesCvTerm getSpeciesCvTerm() {
        return speciesCvTerm;
    }

    public void setSpeciesCvTerm(SpeciesCvTerm speciesCvTerm) {
        this.speciesCvTerm = speciesCvTerm;
    }        

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.name != null ? this.name.hashCode() : 0);
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
        final Sample other = (Sample) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
    
    
}
