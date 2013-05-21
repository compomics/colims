/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import org.hibernate.validator.constraints.Length;

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
    @Basic(optional = false)
    @Column(name = "number")
    private Long number;
    @Basic(optional = true)
    @Length(max = 500, message = "Description must be less than 500 characters")
    @Column(name = "description")
    private String description;
    @JoinColumn(name = "l_project_id", referencedColumnName = "id")
    @ManyToOne
    private Project project;        
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "experiment")
    List<Sample> samples = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "experiment")
    List<BinaryFile> binaryFiles = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "experiment")
    List<SearchAndValidationSettings> searchAndValidationSettings = new ArrayList<>();

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
        hash = 67 * hash + Objects.hashCode(this.id);
        hash = 67 * hash + Objects.hashCode(this.title);
        hash = 67 * hash + Objects.hashCode(this.number);
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
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.number, other.number)) {
            return false;
        }
        return true;
    }    

    @Override
    public String toString() {
        return title;
    }
    
}
