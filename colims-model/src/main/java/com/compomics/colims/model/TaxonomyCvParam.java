package com.compomics.colims.model;

import com.compomics.colims.model.cv.CvParam;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This class represents an taxonomy CV term entity in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "taxonomy_cv_param")
@Entity
public class TaxonomyCvParam extends CvParam {

    private static final long serialVersionUID = -2506922178079767748L;

    /**
     * No-arg constructor.
     */
    public TaxonomyCvParam() {
    }

    /**
     * Constructor.
     *
     * @param ontology the ontology name
     * @param label the ontology label
     * @param accession the CV term accession
     * @param name the CV term name
     */
    public TaxonomyCvParam(final String ontology, final String label, final String accession, final String name) {
        super(ontology, label, accession, name);
    }

}
