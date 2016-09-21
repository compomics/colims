package com.compomics.colims.core.ontology;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * This class holds the mappings between MaxQuant terms and ontology terms.
 * <p>
 * Created by Niels Hulstaert on 11/09/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaxQuantMapping {

    /**
     * The MaxQuant modification terms mappings.
     */
    private Map<String, OntologyTerm> modifications;
    /**
     * The MaxQuant quantification reagent terms mappings.
     */
    private Map<String, OntologyTerm> quantificationReagents;

    /**
     * No-arg constructor.
     */
    public MaxQuantMapping() {
    }

    public Map<String, OntologyTerm> getModifications() {
        return modifications;
    }

    public void setModifications(Map<String, OntologyTerm> modifications) {
        this.modifications = modifications;
    }

    public Map<String, OntologyTerm> getQuantificationReagents() {
        return quantificationReagents;
    }

    public void setQuantificationReagents(Map<String, OntologyTerm> quantificationReagents) {
        this.quantificationReagents = quantificationReagents;
    }
}
