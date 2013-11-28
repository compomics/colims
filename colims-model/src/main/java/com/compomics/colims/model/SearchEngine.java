/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_engine")
@Entity
public class SearchEngine extends AbstractDatabaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    @OneToMany(mappedBy = "searchEngine")
    private List<SearchAndValSetHasSearchEngine> searchAndValSetHasSearchEngines = new ArrayList<>();  

    public List<SearchAndValSetHasSearchEngine> getSearchAndValSetHasSearchEngines() {
        return searchAndValSetHasSearchEngines;
    }

    public void setSearchAndValSetHasSearchEngines(List<SearchAndValSetHasSearchEngine> searchAndValSetHasSearchEngines) {
        this.searchAndValSetHasSearchEngines = searchAndValSetHasSearchEngines;
    }
}
