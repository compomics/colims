/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.model;

import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Niels Hulstaert
 */
@Table(name = "search_and_val_set_has_search_engine")
@Entity 
public class SearchAndValSetHasSearchEngine extends AbstractDatabaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @OneToMany(mappedBy="searchAndValSetHasSearchEngine")
    private List<SearchParameterSettings> searchParametersSettings;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
