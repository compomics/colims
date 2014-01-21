/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_and_val_set_has_search_engine")
@Entity 
public class SearchAndValSetHasSearchEngine extends DatabaseEntity {
    
    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy="searchAndValSetHasSearchEngine")
    private List<SearchParameterSettings> searchParametersSettings = new ArrayList<>();
    @JoinColumn(name = "l_search_engine_id", referencedColumnName = "id")
    @ManyToOne
    private SearchEngine searchEngine;
    @JoinColumn(name = "l_s_and_val_set_id", referencedColumnName = "id")
    @ManyToOne
    private SearchAndValidationSettings searchAndValidationSettings;

    public SearchAndValidationSettings getSearchAndValidationSettings() {
        return searchAndValidationSettings;
    }

    public void setSearchAndValidationSettings(SearchAndValidationSettings searchAndValidationSettings) {
        this.searchAndValidationSettings = searchAndValidationSettings;
    } 
    
    public SearchEngine getSearchEngine() {
        return searchEngine;
    }

    public void setSearchEngine(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    public List<SearchParameterSettings> getSearchParametersSettings() {
        return searchParametersSettings;
    }

    public void setSearchParametersSettings(List<SearchParameterSettings> searchParametersSettings) {
        this.searchParametersSettings = searchParametersSettings;
    }
        
}
