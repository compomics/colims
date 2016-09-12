package com.compomics.colims.core.ontology;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents an ontology term for mapping purposes.
 * <p>
 * Created by Niels Hulstaert on 11/09/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OntologyTerm {

    @JsonProperty(value = "obo_namespace")
    private String oboNamespace;
    @JsonProperty(value = "obo_id")
    private String oboId;
    private String label;

    /**
     * No-arg constructor.
     */
    public OntologyTerm() {
    }

    public String getOboNamespace() {
        return oboNamespace;
    }

    public void setOboNamespace(String oboNamespace) {
        this.oboNamespace = oboNamespace;
    }

    public String getOboId() {
        return oboId;
    }

    public void setOboId(String oboId) {
        this.oboId = oboId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OntologyTerm that = (OntologyTerm) o;

        if (!oboNamespace.equals(that.oboNamespace)) return false;
        if (!oboId.equals(that.oboId)) return false;
        return label != null ? label.equals(that.label) : that.label == null;

    }

    @Override
    public int hashCode() {
        int result = oboNamespace.hashCode();
        result = 31 * result + oboId.hashCode();
        return result;
    }
}
