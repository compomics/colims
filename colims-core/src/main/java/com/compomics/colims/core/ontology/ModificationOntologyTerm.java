package com.compomics.colims.core.ontology;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents a modification ontology term for mapping purposes.
 * <p>
 * Created by Niels Hulstaert on 2/02/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModificationOntologyTerm extends OntologyTerm {

    @JsonProperty(value = "affected_AAs")
    private String affectedAminoAcids;
    @JsonProperty(value = "utilities_name")
    private String utilitiesName;

    public String getAffectedAminoAcids() {
        return affectedAminoAcids;
    }

    public void setAffectedAminoAcids(String affectedAminoAcids) {
        this.affectedAminoAcids = affectedAminoAcids;
    }

    public String getUtilitiesName() {
        return utilitiesName;
    }

    public void setUtilitiesName(String utilitiesName) {
        this.utilitiesName = utilitiesName;
    }
}
