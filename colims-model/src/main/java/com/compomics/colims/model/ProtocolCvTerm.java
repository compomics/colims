package com.compomics.colims.model;

import com.compomics.colims.model.enums.ProtocolCvProperty;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "protocol_cv_term")
@Entity
public class ProtocolCvTerm extends CvTerm {

    @Enumerated(EnumType.STRING)
    private ProtocolCvProperty protocolCvProperty;

    public ProtocolCvProperty getProtocolCvProperty() {
        return protocolCvProperty;
    }

    public void setProtocolCvProperty(ProtocolCvProperty protocolCvProperty) {
        this.protocolCvProperty = protocolCvProperty;
    }            
    
}
