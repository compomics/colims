/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "material")
@Entity
public class Material extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull(message = "A material must have a species")
    @JoinColumn(name = "l_species_cv_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private MaterialCvTerm species;
    @Basic(optional = false)
    @NotNull(message = "A material must have a tissue")
    @JoinColumn(name = "l_tissue_cv_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private MaterialCvTerm tissue;
    @Basic(optional = false)
    @NotNull(message = "A material must have a cell type")
    @JoinColumn(name = "l_cell_type_cv_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private MaterialCvTerm cellType;
    @Basic(optional = false)
    @NotNull(message = "A material must have a compartment")
    @JoinColumn(name = "l_compartment_cv_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private MaterialCvTerm compartment;
    @JoinColumn(name = "l_project_id", referencedColumnName = "id")
    @ManyToOne
    private Project project;
    @ManyToMany(mappedBy = "materials")
    private List<Sample> samples = new ArrayList<>();    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
