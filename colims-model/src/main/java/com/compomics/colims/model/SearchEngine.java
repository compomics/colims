/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import com.compomics.colims.model.enums.BinaryFileType;
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
public class SearchEngine extends DatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * The search engine type
     */
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    protected SearchEngineType searchEngineType;
     /**
     * The version of the search engine
     */
    @Basic(optional = true)
    @Column(name = "version", nullable = true)
    private String version;    
    @OneToMany(mappedBy = "searchEngine")
    private List<SearchAndValSetHasSearchEngine> searchAndValSetHasSearchEngines = new ArrayList<>();  

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

    public List<SearchAndValSetHasSearchEngine> getSearchAndValSetHasSearchEngines() {
        return searchAndValSetHasSearchEngines;
    }

    public void setSearchAndValSetHasSearchEngines(List<SearchAndValSetHasSearchEngine> searchAndValSetHasSearchEngines) {
        this.searchAndValSetHasSearchEngines = searchAndValSetHasSearchEngines;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.searchEngineType);
        hash = 59 * hash + Objects.hashCode(this.version);
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
