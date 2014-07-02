package com.compomics.colims.model;

import com.compomics.colims.model.enums.CvTermType;
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
public abstract class TypedCvTerm extends CvTerm {

    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @Column(name = "cv_property", nullable = false)
    @Enumerated(EnumType.STRING)
    protected CvTermType cvTermType;

    public TypedCvTerm() {
    }

    public TypedCvTerm(final CvTermType cvTermType, final String ontology, final String label, final String accession, final String name) {
        super(ontology, label, accession, name);
        this.cvTermType = cvTermType;        
    }    

    public CvTermType getcvTermType() {
        return cvTermType;
    }

    public void setcvTermType(final CvTermType cvTermType) {
        this.cvTermType = cvTermType;
    }            
 
}
