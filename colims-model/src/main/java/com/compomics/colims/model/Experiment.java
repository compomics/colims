package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an analytical run entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "experiment")
@Entity
public class Experiment extends AuditableDatabaseEntity {

    private static final long serialVersionUID = -5312211553958551386L;

    /**
     * The title of the experiment.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert an experiment title.")
    @Length(min = 5, max = 100, message = "Title must be between {min} and {max} characters.")
    @Column(name = "title", nullable = false, unique = true)
    private String title;
    /**
     * The experiment identifier number.
     */
    @Basic(optional = true)
    @Column(name = "number", nullable = true)
    private Long number;
    /**
     * The description of the experiment. This is a free text field.
     */
    @Basic(optional = true)
    @Length(max = 500, message = "Description must be less than {max} characters.")
    @Column(name = "description", nullable = true)
    private String description;
    /**
     * The storage location of the experiment. This is a free text field.
     */
    @Basic(optional = true)
    @Column(name = "storage_location", nullable = true)
    private String storageLocation;
    /**
     * The project the experiment belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "l_project_id", referencedColumnName = "id")
    private Project project;
    /**
     * The experiment samples.
     */
    @OneToMany(mappedBy = "experiment", cascade = CascadeType.REMOVE)
    List<Sample> samples = new ArrayList<>();
    /**
     * The experiment attachments. These are stored as lob's in the database.
     */
    @OneToMany(mappedBy = "experiment", cascade = CascadeType.ALL)
    List<ExperimentBinaryFile> binaryFiles = new ArrayList<>();

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getTitle() {
        return title;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    public List<ExperimentBinaryFile> getBinaryFiles() {
        return binaryFiles;
    }

    public void setBinaryFiles(List<ExperimentBinaryFile> binaryFiles) {
        this.binaryFiles = binaryFiles;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Experiment that = (Experiment) o;

        if (!title.equals(that.title)) return false;
        if (number != null ? !number.equals(that.number) : that.number != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return !(storageLocation != null ? !storageLocation.equals(that.storageLocation) : that.storageLocation != null);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (storageLocation != null ? storageLocation.hashCode() : 0);
        return result;
    }
}
