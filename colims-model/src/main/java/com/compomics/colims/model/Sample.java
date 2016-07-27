package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @ManyToOne(fetch = FetchType.LAZY)
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
    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL, orphanRemoval = true)
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
    @OneToMany(mappedBy = "sample")
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Sample sample = (Sample) o;

        if (!name.equals(sample.name)) {
            return false;
        }
        if (condition != null ? !condition.equals(sample.condition) : sample.condition != null) {
            return false;
        }
        return !(storageLocation != null ? !storageLocation.equals(sample.storageLocation) : sample.storageLocation != null);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        result = 31 * result + (storageLocation != null ? storageLocation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

}
