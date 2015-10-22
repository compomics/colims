package com.compomics.colims.model.cv;

import com.compomics.colims.model.DatabaseEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Parent class for (non auditable) CV parameter entities.
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
     * No-arg constructor.
     */
    public CvParam() {
    }

    /**
     * Constructor.
     *
     * @param ontology  the ontology name
     * @param label     the ontology label
     * @param accession the CV term accession
     * @param name      the CV term name
     */
    public CvParam(final String ontology, final String label, final String accession, final String name) {
        this.ontology = ontology;
        this.label = label;
        this.accession = accession;
        this.name = name;
    }

    /**
     * Constructor.
     *
     * @param ontology  the ontology name
     * @param label     the ontology label
     * @param accession the CV term accession
     * @param name      the CV term name
     * @param value     the CV term value
     */
    public CvParam(final String ontology, final String label, final String accession, final String name, final String value) {
        this.ontology = ontology;
        this.label = label;
        this.accession = accession;
        this.name = name;
        this.value = value;
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

    @Override
    public String toString() {
        return name + " [" + accession + "]";
    }

}
