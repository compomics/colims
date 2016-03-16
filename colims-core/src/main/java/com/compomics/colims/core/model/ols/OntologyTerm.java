/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.model.ols;

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
    @JsonProperty(value = "ontology_iri")
    private String ontologyIri;
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

    public void setIri(String iri) {
        this.iri = iri;
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

    public void setOntologyNamespace(String ontologyNamespace) {
        this.ontologyNamespace = ontologyNamespace;
    }

    public String getOntologyIri() {
        return ontologyIri;
    }

    public void setOntologyIri(String ontologyIri) {
        this.ontologyIri = ontologyIri;
    }

    public String getShortForm() {
        return shortForm;
    }

    public void setShortForm(String shortForm) {
        this.shortForm = shortForm;
    }

    public String getOboId() {
        return oboId;
    }

    public void setOboId(String oboId) {
        this.oboId = oboId;
    }

    @Override
    public String toString() {
        return label;
    }

}
