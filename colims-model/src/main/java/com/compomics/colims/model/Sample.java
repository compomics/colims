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
    @Column(name = "name")
    private String name;
    @Basic(optional = true)
    @Column(name = "sample_condition")
    private String condition;
    @Basic(optional = true)
    @Column(name = "storage_location")
    private String storageLocation;
    @ManyToOne
    @JoinColumn(name = "l_experiment_id", referencedColumnName = "id")
    private Experiment experiment;
    @ManyToOne
    @JoinColumn(name = "l_protocol_id", referencedColumnName = "id")
    private Protocol protocol;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sample")
    List<SampleBinaryFile> binaryFiles = new ArrayList<>();
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

    public Sample(String name) {
        this.name = name;
    }    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public List<SampleBinaryFile> getBinaryFiles() {
        return binaryFiles;
    }

    public void setBinaryFiles(List<SampleBinaryFile> binaryFiles) {
        this.binaryFiles = binaryFiles;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public List<AnalyticalRun> getAnalyticalRuns() {
        return analyticalRuns;
    }

    public void setAnalyticalRuns(List<AnalyticalRun> analyticalRuns) {
        this.analyticalRuns = analyticalRuns;
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
