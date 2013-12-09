package com.compomics.colims.model;

import com.compomics.colims.model.enums.CvTermType;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public abstract class CvTerm extends AbstractDatabaseEntity {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Column(name = "ontology", nullable = false)
    protected String ontology;
    @Basic(optional = false)
    @Column(name = "label", nullable = false)
    protected String label; 
    @Basic(optional = false)
    @Column(name = "accession", nullable = false, unique = true)
    protected String accession;    
    @Basic(optional = false)
    @Column(name = "name", nullable = false)
    protected String name;
    @Basic(optional = false)
    @Column(name = "cv_property", nullable = false)
    @Enumerated(EnumType.STRING)
    protected CvTermType cvTermType;

    public CvTerm() {
    }

    public CvTerm(CvTermType cvTermType, String ontology, String label, String accession, String name) {
        this.cvTermType = cvTermType;
        this.ontology = ontology;
        this.label = label;
        this.accession = accession;
        this.name = name;        
    }

    public String getOntology() {
        return ontology;
    }

    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CvTermType getcvTermType() {
        return cvTermType;
    }

    public void setcvTermType(CvTermType cvTermType) {
        this.cvTermType = cvTermType;
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CvTerm other = (CvTerm) obj;
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
