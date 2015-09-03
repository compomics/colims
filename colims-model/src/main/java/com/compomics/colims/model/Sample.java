package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * This class represents a sample entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "sample")
@Entity
public class Sample extends AuditableDatabaseEntity {

    private static final long serialVersionUID = -7792823489878347845L;

    /**
     * The sample name.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert a sample name.")
    @Length(min = 5, max = 100, message = "Name must be between {min} and {max} characters.")
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * The sample condition. This is a free text field.
     */
    @Basic(optional = true)
    @Column(name = "sample_condition", nullable = true)
    private String condition;
    /**
     * The sample storage location. This is a free text field.
     */
    @Basic(optional = true)
    @Column(name = "storage_location", nullable = true)
    private String storageLocation;
    /**
     * The experiment the sample belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "l_experiment_id", referencedColumnName = "id")
    private Experiment experiment;
    /**
     * The protocol linked to this sample.
     */
    @ManyToOne
    @JoinColumn(name = "l_protocol_id", referencedColumnName = "id")
    private Protocol protocol;
    /**
     * The list of binary files linked to this sample.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sample")
    List<SampleBinaryFile> binaryFiles = new ArrayList<>();
    /**
     * The materials of this sample.
     */
    @ManyToMany
    @JoinTable(name = "sample_has_material",
            joinColumns = {
                @JoinColumn(name = "l_sample_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                @JoinColumn(name = "l_material_id", referencedColumnName = "id")})
    private List<Material> materials = new ArrayList<>();
    /**
     * The analytical runs that were performed using this sample.
     */
//    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "sample")
    private List<AnalyticalRun> analyticalRuns = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public Sample() {
    }

    /**
     * Constructor.
     *
     * @param name the sample name.
     */
    public Sample(String name) {
        this.name = name;
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
        hash = 73 * hash + Objects.hashCode(this.name);
        hash = 73 * hash + Objects.hashCode(this.condition);
        hash = 73 * hash + Objects.hashCode(this.storageLocation);
        hash = 73 * hash + Objects.hashCode(this.experiment);
        hash = 73 * hash + Objects.hashCode(this.protocol);
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
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.condition, other.condition)) {
            return false;
        }
        if (!Objects.equals(this.storageLocation, other.storageLocation)) {
            return false;
        }
        if (!Objects.equals(this.experiment, other.experiment)) {
            return false;
        }
        return Objects.equals(this.protocol, other.protocol);
    }

    @Override
    public String toString() {
        return name;
    }

}
