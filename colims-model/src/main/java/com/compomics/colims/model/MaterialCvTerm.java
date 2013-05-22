package com.compomics.colims.model;

import com.compomics.colims.model.enums.InstrumentCvProperty;
import com.compomics.colims.model.enums.MaterialCvProperty;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "material_cv_term")
@Entity
public class MaterialCvTerm extends CvTerm {

    @Enumerated(EnumType.STRING)
    private MaterialCvProperty materialCvProperty;

    public MaterialCvProperty getMaterialCvProperty() {
        return materialCvProperty;
    }

    public void setMaterialCvProperty(MaterialCvProperty materialCvProperty) {
        this.materialCvProperty = materialCvProperty;
    }
        
}
