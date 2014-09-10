package com.compomics.colims.model;

import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public abstract class AuditableCvTerm extends AuditableDatabaseEntity {

    private static final long serialVersionUID = -7434489250278743116L;

    @Basic(optional = false)
    @Column(name = "ontology", nullable = false)
    protected String ontology;
    @Basic(optional = false)
    @Column(name = "label", nullable = false)
    protected String label; 
    @Basic(optional = false)
    @Column(name = "accession", nullable = false)
    protected String accession;    
    @Basic(optional = false)
    @Column(name = "name", nullable = false)
    protected String name;

    public AuditableCvTerm() {
    }

    public AuditableCvTerm(final String ontology, final String label, final String accession, final String name) {
        this.ontology = ontology;
        this.label = label;
        this.accession = accession;
        this.name = name;        
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
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.ontology);
        hash = 79 * hash + Objects.hashCode(this.label);
        hash = 79 * hash + Objects.hashCode(this.accession);
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AuditableCvTerm other = (AuditableCvTerm) obj;
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
        return true;
    }

    @Override
    public String toString() {
        return name + " [" + accession + "]";
    }       
 
}
