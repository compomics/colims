package com.compomics.colims.model;

import com.compomics.colims.model.enums.CvTermType;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Niels Hulstaert
 */
@MappedSuperclass
public abstract class CvTerm extends AbstractDatabaseEntity implements Comparable<CvTerm> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    protected Long id;
    @Basic(optional = false)
    @Column(name = "ontology")
    protected String ontology;
    @Basic(optional = false)
    @Column(name = "label")
    protected String label;    
    @Basic(optional = false)
    @Column(name = "accession")
    protected String accession;
    @Basic(optional = false)
    @Column(name = "name")
    protected String name;
    @Column(name = "cv_property")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    
    @Override
    public int compareTo(CvTerm o) {
        return accession.compareTo(o.getAccession());
    }

    /**
     * Convert the CV term properties to a String array.
     *
     * @return
     */
    public String[] toStringArray() {
        String[] stringArray = {cvTermType.toString(), label, accession, name};
        return stringArray;
    }
}
