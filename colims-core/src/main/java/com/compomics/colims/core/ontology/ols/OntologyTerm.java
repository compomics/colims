/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.ontology.ols;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * This class represents an ontology term from the OLS service.
 *
 * @author Niels Hulstaert
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OntologyTerm {

    private String iri;
    private String label;
    private List<String> description;
    @JsonProperty(value = "ontology_name")
    private String ontologyNamespace;
    private String ontologyTitle;
    @JsonProperty(value = "short_form")
    private String shortForm;
    @JsonProperty(value = "obo_id")
    private String oboId;

    /**
     * No-arg constructor.
     */
    public OntologyTerm() {
    }

    public String getIri() {
        return iri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public String getOntologyNamespace() {
        return ontologyNamespace;
    }

    public String getOntologyTitle() {
        return ontologyTitle;
    }

    public void setOntologyTitle(String ontologyTitle) {
        this.ontologyTitle = ontologyTitle;
    }

    public String getShortForm() {
        return shortForm;
    }

    public String getOboId() {
        return oboId;
    }

    @Override
    public String toString() {
        return label;
    }

    /**
     * Copy the fields of the given ontology term to this instance.
     *
     * @param ontologyTermToCopy
     */
    public void copy(OntologyTerm ontologyTermToCopy) {
        this.iri = ontologyTermToCopy.getIri();
        this.label = ontologyTermToCopy.getLabel();
        this.description = ontologyTermToCopy.getDescription();
        this.ontologyNamespace = ontologyTermToCopy.getOntologyNamespace();
        this.ontologyTitle = ontologyTermToCopy.getOntologyTitle();
        this.oboId = ontologyTermToCopy.getOboId();
        this.shortForm = ontologyTermToCopy.shortForm;
    }

}
