package com.compomics.colims.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Material material = (Material) o;

        if (!name.equals(material.name)) return false;
        if (species != null ? !species.equals(material.species) : material.species != null) return false;
        if (tissue != null ? !tissue.equals(material.tissue) : material.tissue != null) return false;
        if (cellType != null ? !cellType.equals(material.cellType) : material.cellType != null) return false;
        return !(compartment != null ? !compartment.equals(material.compartment) : material.compartment != null);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (species != null ? species.hashCode() : 0);
        result = 31 * result + (tissue != null ? tissue.hashCode() : 0);
        result = 31 * result + (cellType != null ? cellType.hashCode() : 0);
        result = 31 * result + (compartment != null ? compartment.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

}
