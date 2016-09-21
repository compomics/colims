package com.compomics.colims.model.cv;

import com.compomics.colims.model.AuditableDatabaseEntity;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Parent class for auditable CV parameter entities.
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public abstract class AuditableCvParam extends AuditableDatabaseEntity {

    private static final long serialVersionUID = -7434489250278743116L;

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
    public AuditableCvParam() {
    }

    /**
     * Constructor.
     *
     * @param label     the ontology label
     * @param accession the CV term accession
     * @param name      the CV term name
     */
    public AuditableCvParam(final String label, final String accession, final String name) {

        this.label = label;
        this.accession = accession;
        this.name = name;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditableCvParam that = (AuditableCvParam) o;

        if (!label.equals(that.label)) return false;
        if (!accession.equals(that.accession)) return false;
        if (!name.equals(that.name)) return false;
        return !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.label);
        hash = 47 * hash + Objects.hashCode(this.accession);
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public String toString() {
        return name + " [" + accession + "]";
    }

}
