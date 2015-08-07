package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * This class represents a material entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "material")
@Entity
public class Material extends AuditableDatabaseEntity {

    private static final long serialVersionUID = -976001398433451179L;

    /**
     * The material name.
     */
    @Basic(optional = false)
    @NotBlank(message = "Please insert an instrument name.")
    @Length(min = 3, max = 30, message = "Name must be between {min} and {max} characters.")
    @Column(name = "name", nullable = false, unique = false)
    private String name;
    /**
     * The mandatory species CV term.
     */
    @NotNull(message = "A material must have a species.")
    @JoinColumn(name = "l_species_cv_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private MaterialCvParam species;
    /**
     * The optional tissue CV term.
     */
    @JoinColumn(name = "l_tissue_cv_id", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private MaterialCvParam tissue;
    /**
     * The optional cell type CV term.
     */
    @JoinColumn(name = "l_cell_type_cv_id", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private MaterialCvParam cellType;
    /**
     * The optional compartment CV term.
     */
    @JoinColumn(name = "l_compartment_cv_id", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private MaterialCvParam compartment;
    /**
     * The project in which the material was analyzed.
     */
    @JoinColumn(name = "l_project_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;
    /**
     * The samples that originate from this material.
     */
    @ManyToMany(mappedBy = "materials")
    private List<Sample> samples = new ArrayList<>();

    /**
     * No-arg constructor.
     */
    public Material() {
    }

    /**
     * Constructor.
     *
     * @param name the material name
     */
    public Material(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MaterialCvParam getSpecies() {
        return species;
    }

    public void setSpecies(MaterialCvParam species) {
        this.species = species;
    }

    public MaterialCvParam getTissue() {
        return tissue;
    }

    public void setTissue(MaterialCvParam tissue) {
        this.tissue = tissue;
    }

    public MaterialCvParam getCellType() {
        return cellType;
    }

    public void setCellType(MaterialCvParam cellType) {
        this.cellType = cellType;
    }

    public MaterialCvParam getCompartment() {
        return compartment;
    }

    public void setCompartment(MaterialCvParam compartment) {
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
        return Objects.equals(this.project, other.project);
    }

    @Override
    public String toString() {
        return name;
    }

}
