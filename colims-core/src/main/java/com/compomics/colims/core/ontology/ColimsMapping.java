package com.compomics.colims.core.ontology;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * This class holds the mappings between Colims terms and ontology terms.
 * <p>
 * Created by Niels Hulstaert on 11/09/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ColimsMapping {

    /**
     * The Colims quantification method terms mappings.
     */
    private Map<String, OntologyTerm> quantificationMethods;

    /**
     * No-arg constructor.
     */
    public ColimsMapping() {
    }

    public Map<String, OntologyTerm> getQuantificationMethods() {
        return quantificationMethods;
    }

    public void setQuantificationMethods(Map<String, OntologyTerm> quantificationMethods) {
        this.quantificationMethods = quantificationMethods;
    }
}
