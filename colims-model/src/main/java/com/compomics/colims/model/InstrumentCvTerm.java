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

    public InstrumentCvProperty getInstrumentCvProperty() {
        return instrumentCvProperty;
    }

    public void setInstrumentCvProperty(InstrumentCvProperty instrumentCvProperty) {
        this.instrumentCvProperty = instrumentCvProperty;
    }
}
