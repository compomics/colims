package com.compomics.colims.model.cv;

import com.compomics.colims.model.DatabaseEntity;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public abstract class CvParam extends DatabaseEntity {

    private static final long serialVersionUID = 3395161527675025740L;

    /**
     * The full name of the CV.
     */
    @Basic(optional = false)
    @Column(name = "ontology", nullable = false)
    protected String ontology;
    /**
     * The label of the CV.
     */
    @Basic(optional = false)
    @Column(name = "label", nullable = false)
    protected String label;
    /**
     * The accession or ID number of this CV term in the source CV.
     */
    @Basic(optional = false)
    @Column(name = "accession", nullable = false)
    protected String accession;
    /**
     * The name of the parameter.
     */
    @Basic(optional = false)
    @Column(name = "name", nullable = false)
    protected String name;
    /**
     * The user-entered value of the parameter.
     */
    @Basic(optional = true)
    @Column(name = "param_value", nullable = true)
    protected String value;
    /**
     * The full name of the unit CV.
     */
    @Basic(optional = true)
    @Column(name = "unit_ontology", nullable = true)
    protected String unitOntology;
    /**
     * The label of the unit CV.
     */
    @Basic(optional = true)
    @Column(name = "unit_label", nullable = true)
    protected String unitLabel;
    /**
     * The accession or ID number of this unit CV term in the source unit CV.
     */
    @Basic(optional = true)
    @Column(name = "unit_accession", nullable = true)
    protected String unitAccession;
    /**
     * The name of the unit parameter.
     */
    @Basic(optional = true)
    @Column(name = "unit_name", nullable = true)
    protected String unitName;

    public CvParam() {
    }

    public CvParam(final String ontology, final String label, final String accession, final String name) {
        this.ontology = ontology;
        this.label = label;
        this.accession = accession;
        this.name = name;
    }

    public CvParam(String ontology, String label, String accession, String name, String value, String unitOntology, String unitLabel, String unitAccession, String unitName) {
        this.ontology = ontology;
        this.label = label;
        this.accession = accession;
        this.name = name;
        this.value = value;
        this.unitOntology = unitOntology;
        this.unitLabel = unitLabel;
        this.unitAccession = unitAccession;
        this.unitName = unitName;
    }

    public String getOntology() {
        return ontology;
    }

    public void setOntology(final String ontology) {
        this.ontology = ontology;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(final String accession) {
        this.accession = accession;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnitOntology() {
        return unitOntology;
    }

    public void setUnitOntology(String unitOntology) {
        this.unitOntology = unitOntology;
    }

    public String getUnitLabel() {
        return unitLabel;
    }

    public void setUnitLabel(String unitLabel) {
        this.unitLabel = unitLabel;
    }

    public String getUnitAccession() {
        return unitAccession;
    }

    public void setUnitAccession(String unitAccession) {
        this.unitAccession = unitAccession;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.ontology);
        hash = 29 * hash + Objects.hashCode(this.label);
        hash = 29 * hash + Objects.hashCode(this.accession);
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.value);
        hash = 29 * hash + Objects.hashCode(this.unitOntology);
        hash = 29 * hash + Objects.hashCode(this.unitLabel);
        hash = 29 * hash + Objects.hashCode(this.unitAccession);
        hash = 29 * hash + Objects.hashCode(this.unitName);
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
        final CvParam other = (CvParam) obj;
        if (!Objects.equals(this.ontology, other.ontology)) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        if (!Objects.equals(this.accession, other.accession)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.unitOntology, other.unitOntology)) {
            return false;
        }
        if (!Objects.equals(this.unitLabel, other.unitLabel)) {
            return false;
        }
        if (!Objects.equals(this.unitAccession, other.unitAccession)) {
            return false;
        }
        if (!Objects.equals(this.unitName, other.unitName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name + " [" + accession + "]";
    }

}
