/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.model.ols;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents an ontology from the OLS service.
 *
 * @author Niels Hulstaert
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ontology {

    /**
     * Stub ontology for searching terms in all available ontologies.
     */
    public static final Ontology ALL_ONTOLOGIES = new Ontology();

    static {
        ALL_ONTOLOGIES.setBaseUris(new ArrayList<>());
        ALL_ONTOLOGIES.setNameSpace("");
        ALL_ONTOLOGIES.setPrefix("");
        ALL_ONTOLOGIES.setTitle("Search all ontologies");
    }

    @JsonProperty(value = "namespace")
    private String nameSpace;
    @JsonProperty(value = "preferredPrefix")
    private String prefix;
    private String title;
    private List<String> baseUris;

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getBaseUris() {
        return baseUris;
    }

    public void setBaseUris(List<String> baseUris) {
        this.baseUris = baseUris;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.nameSpace);
        hash = 83 * hash + Objects.hashCode(this.prefix);
        hash = 83 * hash + Objects.hashCode(this.title);
        hash = 83 * hash + Objects.hashCode(this.baseUris);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ontology other = (Ontology) obj;
        if (!Objects.equals(this.nameSpace, other.nameSpace)) {
            return false;
        }
        if (!Objects.equals(this.prefix, other.prefix)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.baseUris, other.baseUris)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (!nameSpace.isEmpty()) {
            return title + " (" + nameSpace + ")";
        } else {
            return title;
        }
    }

}
