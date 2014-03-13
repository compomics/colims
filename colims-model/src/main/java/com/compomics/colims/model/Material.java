/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "material")
@Entity
public class Material extends AuditableDatabaseEntity {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotBlank(message = "Please insert an instrument name")
    @Length(min = 3, max = 30, message = "Name must be between {min} and {max} characters")
    @Column(name = "name", nullable = false, unique = false)
    private String name;
    @Basic(optional = false)
    @NotNull(message = "A material must have a species")
    @JoinColumn(name = "l_species_cv_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private MaterialCvTerm species;
    @Basic(optional = true)
    @JoinColumn(name = "l_tissue_cv_id", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private MaterialCvTerm tissue;
    @Basic(optional = true)
    @JoinColumn(name = "l_cell_type_cv_id", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private MaterialCvTerm cellType;
    @Basic(optional = true)
    @JoinColumn(name = "l_compartment_cv_id", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private MaterialCvTerm compartment;
    @JoinColumn(name = "l_project_id", referencedColumnName = "id")
    @ManyToOne
    private Project project;
    @ManyToMany(mappedBy = "materials")
    private List<Sample> samples = new ArrayList<>();

    public Material() {
    }

    public Material(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MaterialCvTerm getSpecies() {
        return species;
    }

    public void setSpecies(MaterialCvTerm species) {
        this.species = species;
    }

    public MaterialCvTerm getTissue() {
        return tissue;
    }

    public void setTissue(MaterialCvTerm tissue) {
        this.tissue = tissue;
    }

    public MaterialCvTerm getCellType() {
        return cellType;
    }

    public void setCellType(MaterialCvTerm cellType) {
        this.cellType = cellType;
    }

    public MaterialCvTerm getCompartment() {
        return compartment;
    }

    public void setCompartment(MaterialCvTerm compartment) {
        this.compartment = compartment;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.name);
        hash = 43 * hash + Objects.hashCode(this.species);
        hash = 43 * hash + Objects.hashCode(this.tissue);
        hash = 43 * hash + Objects.hashCode(this.cellType);
        hash = 43 * hash + Objects.hashCode(this.compartment);
        hash = 43 * hash + Objects.hashCode(this.project);
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
        final Material other = (Material) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.species, other.species)) {
            return false;
        }
        if (!Objects.equals(this.tissue, other.tissue)) {
            return false;
        }
        if (!Objects.equals(this.cellType, other.cellType)) {
            return false;
        }
        if (!Objects.equals(this.compartment, other.compartment)) {
            return false;
        }
        if (!Objects.equals(this.project, other.project)) {
            return false;
        }
        return true;
    }    

    @Override
    public String toString() {
        return name;
    }
}
