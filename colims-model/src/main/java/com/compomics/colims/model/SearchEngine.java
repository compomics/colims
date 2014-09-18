/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.enums.SearchEngineType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_engine")
@Entity
public class SearchEngine extends CvTerm {
    
    private static final long serialVersionUID = -5428696863055618148L;
    private static final String NOT_APPLICABLE = "N/A";

    /**
     * The search engine type.
     */
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    protected SearchEngineType searchEngineType;
    /**
     * The version of the search engine.
     */
    @Basic(optional = true)
    @Column(name = "version", nullable = true)
    private String version;
    @OneToMany(mappedBy = "searchEngine")
    private List<SearchAndValidationSettings> searchAndValidationSettingses = new ArrayList<>();

    public SearchEngine() {
    }

    public SearchEngine(final SearchEngineType searchEngineType, final String version) {
        super(NOT_APPLICABLE, NOT_APPLICABLE, NOT_APPLICABLE, NOT_APPLICABLE);
        this.searchEngineType = searchEngineType;
        this.version = version;
    }

    public SearchEngine(SearchEngineType searchEngineType, final String version, final String ontology, final String label, final String accession, final String name) {
        super(ontology, label, accession, name);
        this.searchEngineType = searchEngineType;
        this.version = version;
    }

    /**
     * Constructor that creates a new instance with all fields of the given
     * SearchEngine and the given version.
     *
     * @param searchEngine the SearchEngine to copy
     * @param version the search engine version
     */
    public SearchEngine(final SearchEngine searchEngine, final String version) {
        this(searchEngine.getSearchEngineType(), version, searchEngine.getOntology(), searchEngine.getLabel(), searchEngine.getAccession(), searchEngine.getName());
    }

    public SearchEngineType getSearchEngineType() {
        return searchEngineType;
    }

    public void setSearchEngineType(SearchEngineType searchEngineType) {
        this.searchEngineType = searchEngineType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<SearchAndValidationSettings> getSearchAndValidationSettingses() {
        return searchAndValidationSettingses;
    }

    public void setSearchAndValidationSettingses(List<SearchAndValidationSettings> searchAndValidationSettingses) {
        this.searchAndValidationSettingses = searchAndValidationSettingses;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.searchEngineType);
        hash = 97 * hash + Objects.hashCode(this.version);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SearchEngine other = (SearchEngine) obj;
        if (this.searchEngineType != other.searchEngineType) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        return true;
    }    

}
