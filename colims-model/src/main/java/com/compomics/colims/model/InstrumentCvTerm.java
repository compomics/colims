package com.compomics.colims.model;

import com.compomics.colims.model.enums.CvTermProperty;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "instrument_cv_term")
@Entity
public class InstrumentCvTerm extends CvTerm {

    public InstrumentCvTerm() {
    }

    public InstrumentCvTerm(CvTermProperty cvTermProperty, String ontology, String label, String accession, String name) {
        super(cvTermProperty, ontology, label, accession, name);
    }    

    @Override
    public String toString() {
        return super.toString();
    }

}
