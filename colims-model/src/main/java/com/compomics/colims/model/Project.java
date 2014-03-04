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
 *
 * @author Niels Hulstaert
 */
@Table(name = "project")
@Entity
public class Project extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @NotBlank(message = "Please insert a project title")
    @Length(min = 5, max = 100, message = "Title must be between {min} and {max} characters")
    @Column(name = "title", nullable = false, unique = true)
    private String title;
    @Basic(optional = false)
    @NotBlank(message = "Please insert a project label")
    @Length(min = 3, max = 20, message = "Label must be between {min} and {max} characters")
    @Column(name = "label", nullable = false)
    private String label;
    @Basic(optional = true)
    @Length(max = 500, message = "Description must be less than {max} characters")
    @Column(name = "description", nullable = true)
    private String description;
    @Basic(optional = false)
    @ManyToOne
    @JoinColumn(name = "l_owner_user_id", referencedColumnName = "id", nullable = false)    
    private User owner;
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "project_has_user",
            joinColumns = {
        @JoinColumn(name = "l_project_id", referencedColumnName = "id")},
            inverseJoinColumns = {
        @JoinColumn(name = "l_user_id", referencedColumnName = "id")})
    private List<User> users = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private List<Experiment> experiments = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private List<Material> materials = new ArrayList<>();

    public Project() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }        

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Experiment> getExperiments() {
        return experiments;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }        

    public void setExperiments(List<Experiment> experiments) {
        this.experiments = experiments;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.title);
        hash = 97 * hash + Objects.hashCode(this.label);
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
        final Project other = (Project) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        return true;
    }

    

    @Override
    public String toString() {
        return title;
    }
}
