/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "experiment")
@Entity
public class Experiment extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "title")
    private String title;
    @JoinColumn(name = "l_project_id", referencedColumnName = "id")
    @ManyToOne
    private Project project;
    @JoinColumn(name = "l_protocol_id", referencedColumnName = "id")
    @ManyToOne
    private Protocol protocol;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "experiment")
    List<ExperimentParam> experimentParams;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "experiment")
    List<Sample> samples;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "experiment")
    List<BinaryFile> binaryFiles;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "experiment")
    List<SearchAndValidationSettings> searchAndValidationSettings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }        

    public List<ExperimentParam> getExperimentParams() {
        return experimentParams;
    }

    public void setExperimentParams(List<ExperimentParam> experimentParams) {
        this.experimentParams = experimentParams;
    }

    public String getTitle() {
        return title;
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

    public List<BinaryFile> getBinaryFiles() {
        return binaryFiles;
    }

    public void setBinaryFiles(List<BinaryFile> binaryFiles) {
        this.binaryFiles = binaryFiles;
    }

    public List<SearchAndValidationSettings> getSearchAndValidationSettings() {
        return searchAndValidationSettings;
    }

    public void setSearchAndValidationSettings(List<SearchAndValidationSettings> searchAndValidationSettings) {
        this.searchAndValidationSettings = searchAndValidationSettings;
    }        

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 79 * hash + (this.project != null ? this.project.hashCode() : 0);
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
        final Experiment other = (Experiment) obj;
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if (this.project != other.project && (this.project == null || !this.project.equals(other.project))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return title;
    }
    
}
