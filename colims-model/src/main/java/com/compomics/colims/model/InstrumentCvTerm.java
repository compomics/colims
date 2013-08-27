package com.compomics.colims.model;

import com.compomics.colims.model.enums.InstrumentCvProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "instrument_cv_term")
@Entity
public class InstrumentCvTerm extends CvTerm {

    @Column(name = "cv_property")
    @Enumerated(EnumType.STRING)
    private InstrumentCvProperty instrumentCvProperty;

    public InstrumentCvTerm() {
    }

    public InstrumentCvTerm(InstrumentCvProperty instrumentCvProperty, String ontology, String label, String accession, String name) {
        super(ontology, label, accession, name);
        this.instrumentCvProperty = instrumentCvProperty;
    }

    public InstrumentCvProperty getInstrumentCvProperty() {
        return instrumentCvProperty;
    }

    public void setInstrumentCvProperty(InstrumentCvProperty instrumentCvProperty) {
        this.instrumentCvProperty = instrumentCvProperty;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String[] toStringArray() {
        String[] stringArray = {instrumentCvProperty.toString(), label, accession, name};
        return stringArray;
    }
}
